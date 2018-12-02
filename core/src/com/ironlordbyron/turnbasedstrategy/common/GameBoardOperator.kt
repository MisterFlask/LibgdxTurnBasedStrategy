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
import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.foreverHighlightBlinking
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
                                            val actionRunner: ActionRunner) : EventListener {
    override fun consumeEvent(event: TacticalGuiEvent) {

    }

    private var actionQueue = ArrayList<ActorActionPair>()

    public fun endTurn() {
        println("End turn clicked.  TODO!")

        runEnemyTurn()
    }

    private fun runEnemyTurn() {
        actionQueue =  ArrayList()
        for (enemyCharacter in boardState.listOfEnemyCharacters) {
            val ai = enemyAiFactory.getEnemyAi(enemyCharacter.tacMapUnit.enemyAiType)
            val nextActions = ai.getNextActions(enemyCharacter);
            for (action in nextActions){
                when(action){
                    is AiPlannedAction.MoveToTile -> (moveCharacterToTile(enemyCharacter, action.to, true))
                }
            }
        }
        eventNotifier.notifyListeners(TacticalGuiEvent.FinishedEnemyTurn())
        actionRunner.runThroughActionQueue(actionQueue, finalAction = {})
        actionQueue = ArrayList()
    }


    init{
        eventNotifier.registerListener(this)
    }
    private val listOfHighlights = ArrayList<Actor>()

    // moves the character to the given tile logically, and returns the actor/action pair for animation purposes.
    fun moveCharacterToTile(character: LogicalCharacter, toTile: TileLocation, waitOnQueuedActions: Boolean) : ActorActionPair{
        character.tileLocation = toTile
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(toTile)
        var moveAction : Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f)
        val result = ActorActionPair(actor = character.actor, action = moveAction)
        actionQueue.add(result)
        if (!waitOnQueuedActions){
            // TODO: This is probably wrong.
            actionRunner.runThroughActionQueue(actionQueue, finalAction = {})
            actionQueue = ArrayList()
        }
        return result

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

    fun highlightTiles(tiles: Collection<TileLocation>,
                       highlightType: HighlightType,
                       action: Action? = null) {
        var realAction = action
        if (realAction == null){
            realAction = foreverHighlightBlinking()
        }
        val texture = tileMapOperationsHandler.pullGenericTexture(
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        for (location in tiles) {
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, texture,
                    alpha = .5f)
            actor.addAction(realAction)
            listOfHighlights.add(actor)
        }
    }

}


