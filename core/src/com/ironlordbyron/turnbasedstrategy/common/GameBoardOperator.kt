package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.ai.AiPlannedAction
import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiFactory
import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates.CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.CharacterSpriteUtils
import com.ironlordbyron.turnbasedstrategy.view.animation.*
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject
import javax.inject.Singleton





/**
 * Responsible for coordinating game-level actions between lower-level actors like the tile map operations handler
 * and the character image processor.
 * Acts as a facade that should not include raw images and such in its interface.
 */
@Singleton
class GameBoardOperator @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                                            val tileMapProvider: TileMapProvider,
                                            val characterImageManager: CharacterImageManager,
                                            val eventNotifier: EventNotifier,
                                            val logicalTileTracker: LogicalTileTracker,
                                            val imageActorFactory: SpriteActorFactory,
                                            val boardState: TacticalMapState,
                                            val enemyAiFactory:EnemyAiFactory,
                                            val actionRunner: ActionRunner,
                                            val characterSpriteUtils: CharacterSpriteUtils) : EventListener {
    override fun consumeEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.FinishedEnemyTurn -> {
                startPlayerTurn()
            }
        }
    }

    private fun startPlayerTurn() {
        for (unit in boardState.listOfCharacters){
            unit.movedThisTurn = false
            characterSpriteUtils.brightenSprite(unit)
        }
    }

    init{
        eventNotifier.registerListener(this)
    }

    private var actionQueue = ArrayList<ActorActionPair>()

    public fun endTurn() {
        runEnemyTurn()
    }

    private fun runEnemyTurn() {
        actionQueue =  ArrayList()
        for (enemyCharacter in boardState.listOfEnemyCharacters) {
            val ai = enemyAiFactory.getEnemyAi(enemyCharacter.tacMapUnit.enemyAiType)
            val nextActions = ai.getNextActions(enemyCharacter);
            for (action in nextActions){
                when(action){
                    is AiPlannedAction.MoveToTile -> moveCharacterToTile(enemyCharacter,
                            action.to,
                            waitOnMoreQueuedActions = true,
                            wasPlayerInitiated = false)
                }
            }
        }
        println("Action queue has elements: $actionQueue")
        actionRunner.runThroughActionQueue(actionQueue, finalAction = {
            eventNotifier.notifyListeners(TacticalGuiEvent.FinishedEnemyTurn())
        })
        actionQueue = ArrayList()
    }

    private val listOfHighlights = ArrayList<Actor>()

    // moves the character to the given tile logically, and returns the actor/action pair for animation purposes.
    fun moveCharacterToTile(character: LogicalCharacter, toTile: TileLocation, waitOnMoreQueuedActions: Boolean,
                            wasPlayerInitiated: Boolean){
        if (!wasPlayerInitiated) {
            // first, show the player where the ai COULD move to
            val tilesToHighlight = boardState.getWhereCharacterCanMoveTo(character)
            val actorActionPairForHighlights = getTileHighlightActorActionPairs(tilesToHighlight, HighlightType.RED_TILE)
            actionQueue.add(actorActionPairForHighlights)
        }

        // next, move the character.
        character.tileLocation = toTile
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(toTile)
        var moveAction : Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f)
        val result = ActorActionPair(actor = character.actor, action = moveAction)
        actionQueue.add(result)
        actionQueue.add(SpriteColorActorAction.build(character, SpriteColorActorAction.DIM_COLOR))
        if (!waitOnMoreQueuedActions){
            actionRunner.runThroughActionQueue(actionQueue, finalAction = {})
            actionQueue = ArrayList()
        }

        // now mark the character as moved by darkening the sprite.
    }

    fun removeCharacter(character: LogicalCharacter) {
        boardState.listOfCharacters.remove(character)
        character.actor.remove()
    }

    fun addCharacterToTile(tacMapUnit: TacMapUnitTemplate, tileLocation: TileLocation, playerControlled: Boolean) {
        val actor = characterImageManager.placeCharacterSprite(tileMapProvider.tiledMap, tileLocation,
                tileMapOperationsHandler.pullTextureFromTilemap(CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE, tacMapUnit.tiledTexturePath.spriteId, tacMapUnit.tiledTexturePath.tileSetName))
        boardState.listOfCharacters.add(LogicalCharacter(actor, tileLocation, tacMapUnit, playerControlled))

    }

    fun killHighlights() {
        listOfHighlights.forEach { it.remove() }
        listOfHighlights.removeAll{true}
    }

    public enum class ActionGeneratorType{
        HIGHLIGHT_UNTIL_FURTHER_NOTICE
    }

    fun highlightTiles(tiles: Collection<TileLocation>,
                       highlightType: HighlightType,
                       actionGenerator: ActionGeneratorType = ActionGeneratorType.HIGHLIGHT_UNTIL_FURTHER_NOTICE) {
        val texture = tileMapOperationsHandler.pullGenericTexture(
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        for (location in tiles) {
            val actionToApply = when(actionGenerator){
                ActionGeneratorType.HIGHLIGHT_UNTIL_FURTHER_NOTICE -> foreverHighlightBlinking()
            }
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, texture,
                    alpha = .5f)
            actor.addAction(actionToApply)
            listOfHighlights.add(actor)
        }
    }

    fun getTileHighlightActorActionPairs(tiles: Collection<TileLocation>,
                                         highlightType: HighlightType) : ActorActionPair{
        val actorActionPairList = ArrayList<ActorActionPair>()
        val texture = tileMapOperationsHandler.pullGenericTexture(
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        for (location in tiles) {
            val action = temporaryHighlightBlinking()
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, texture,
                    alpha = .0f)
            actorActionPairList.add(ActorActionPair(actor, action))
        }
        val actorActionPair = ActorActionPair(actor = actorActionPairList[0].actor,
                action = actorActionPairList[0].action,
                secondaryActions = actorActionPairList.subList(1, actorActionPairList.size),
                murderActorsOnceCompletedAnimation = true,
                name="temporaryHighlights")

        return actorActionPair
    }

}


