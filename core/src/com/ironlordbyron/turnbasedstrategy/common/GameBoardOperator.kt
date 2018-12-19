package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates.CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.MapHighlighter
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.CharacterSpriteUtils
import com.ironlordbyron.turnbasedstrategy.view.animation.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
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
                                            val actionRunner: ActionRunner,
                                            val characterSpriteUtils: CharacterSpriteUtils,
                                            val mapHighlighter: MapHighlighter,
                                            val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                            val temporaryAnimationGenerator: TemporaryAnimationGenerator,
                                            val floatingTextGenerator: FloatingTextGenerator,
                                            val deathAnimationGenerator: DeathAnimationGenerator) : EventListener {


    // HACK: This shouldn't be public.
    public var actionQueue = ArrayList<ActorActionPair>()

    override fun consumeGuiEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.FinishedEnemyTurn -> {
                startPlayerTurn()
            }
        }
    }

    public fun clearQueue(){
        actionQueue = ArrayList()
    }

    private fun startPlayerTurn() {
        for (unit in boardState.listOfCharacters){
            unit.actionsLeft = unit.maxActionsLeft
            characterSpriteUtils.brightenSprite(unit)
        }
    }

    init{
        eventNotifier.registerGuiListener(this)
    }


    // moves the character to the given tile logically, and returns the actor/action pair for animation purposes.
    fun moveCharacterToTile(character: LogicalCharacter, toTile: TileLocation, waitOnMoreQueuedActions: Boolean,
                            wasPlayerInitiated: Boolean){
        if (!wasPlayerInitiated) {
            // first, show the player where the ai COULD move to
            val tilesToHighlight = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(character)
            val actorActionPairForHighlights = mapHighlighter.getTileHighlightActorActionPairs(tilesToHighlight, HighlightType.RED_TILE)
            actionQueue.add(actorActionPairForHighlights)
        }

        // next, move the character.
        boardState.moveCharacterToTile(character, toTile)
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(toTile)
        var moveAction : Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f)
        val result = ActorActionPair(actor = character.actor, action = moveAction)
        actionQueue.add(result)
        if (character.endedTurn){
            actionQueue.add(SpriteColorActorAction.build(character, SpriteColorActorAction.DIM_COLOR))
        }
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

    fun damageCharacter(targetCharacter: LogicalCharacter,
                        waitOnMoreQueuedActions: Boolean = false,
                        damageAmount: Int) {
        targetCharacter.healthLeft -= damageAmount // TODO: Not the responsibility of this class
        actionQueue.add(temporaryAnimationGenerator.getTemporaryAnimationActorActionPair(targetCharacter.tileLocation))
        actionQueue.add(floatingTextGenerator.getTemporaryAnimationActorActionPair("${damageAmount}", targetCharacter.tileLocation))
        if (targetCharacter.isDead){
            actionQueue.add(deathAnimationGenerator.turnCharacterSideways(targetCharacter))
        }
        if (!waitOnMoreQueuedActions){
            actionRunner.runThroughActionQueue(actionQueue, finalAction = {})
            actionQueue = ArrayList()
        }
    }

    fun zoomToFocusOnTile(){

    }

}


