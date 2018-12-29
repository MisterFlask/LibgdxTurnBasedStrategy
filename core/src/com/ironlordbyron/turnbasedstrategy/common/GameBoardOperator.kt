package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.controller.*
import com.ironlordbyron.turnbasedstrategy.view.CharacterSpriteUtils
import com.ironlordbyron.turnbasedstrategy.view.animation.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.DeathAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.FloatingTextGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.TemporaryAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.UnitSpawnAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import javax.inject.Inject
import javax.inject.Singleton



public class CharacterSpawner @Inject constructor(
        val characterImageManager: CharacterImageManager,
        val boardState: TacticalMapState,
        val eventNotifier: EventNotifier
        ){
    fun addCharacterToTile(tacMapUnit: TacMapUnitTemplate, tileLocation: TileLocation, playerControlled: Boolean) {
        val actor = characterImageManager.placeCharacterActor(tileLocation,tacMapUnit.tiledTexturePath)
        val characterSpawned = LogicalCharacter(actor, tileLocation, tacMapUnit, playerControlled)
        boardState.listOfCharacters.add(characterSpawned)
        eventNotifier.notifyListenersOfGameEvent(TacticalGameEvent.UnitSpawned(characterSpawned))
    }

}

/**
 * Responsible for coordinating game-level actions between lower-level actors like the tile map operations handler
 * and the character image processor.
 * Acts as a facade that should not include raw images and such in its interface.
 * Responsible for handling animations
 */
@Singleton
class GameBoardOperator @Inject constructor(val tiledMapOperationsHandler: TiledMapOperationsHandler,
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
                                            val deathAnimationGenerator: DeathAnimationGenerator,
                                            val actionQueueProvider: ActionQueueProvider,
                                            val unitSpawnAnimator: UnitSpawnAnimationGenerator) : EventListener{


    override fun consumeGuiEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.FinishedEnemyTurn -> {
                startPlayerTurn()
            }
        }
    }

    override fun consumeGameEvent(event: TacticalGameEvent) {
        when(event){
            is TacticalGameEvent.UnitSpawned -> {
                actionQueueProvider.addAction(unitSpawnAnimator.createUnitSpawnAnimation(event.character))
            }
        }
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
            actionQueueProvider.addAction(actorActionPairForHighlights)
        }

        // next, move the character.
        boardState.moveCharacterToTile(character, toTile)
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(toTile)
        var moveAction : Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f)
        val result = ActorActionPair(actor = character.actor, action = moveAction)
        actionQueueProvider.addAction(result)
        if (character.endedTurn){
            actionQueueProvider.addAction(SpriteColorActorAction.build(character, SpriteColorActorAction.DIM_COLOR))
        }
        if (!waitOnMoreQueuedActions){
            actionQueueProvider.runThroughActionQueue(finalAction = {})
            actionQueueProvider.clearQueue()
        }

        // now mark the character as moved by darkening the sprite.
    }

    fun removeCharacter(character: LogicalCharacter) {
        boardState.listOfCharacters.remove(character)
        character.actor.remove()
    }

    fun addCharacterToTile(tacMapUnit: TacMapUnitTemplate, tileLocation: TileLocation, playerControlled: Boolean) : LogicalCharacter {
        val actor = characterImageManager.placeCharacterActor(tileLocation,tacMapUnit.tiledTexturePath)
        val character=  LogicalCharacter(actor, tileLocation, tacMapUnit, playerControlled)
        boardState.listOfCharacters.add(character)
        return character
    }

    fun damageCharacter(targetCharacter: LogicalCharacter,
                        waitOnMoreQueuedActions: Boolean = false,
                        damageAmount: Int) {
        targetCharacter.healthLeft -= damageAmount // TODO: Not the responsibility of this class
        actionQueueProvider.addAction(temporaryAnimationGenerator.getTemporaryAnimationActorActionPair(targetCharacter.tileLocation, DataDrivenOnePageAnimation.EXPLODE))
        actionQueueProvider.addAction(floatingTextGenerator.getTemporaryAnimationActorActionPair("${damageAmount}", targetCharacter.tileLocation))
        if (targetCharacter.isDead){
            actionQueueProvider.addAction(deathAnimationGenerator.turnCharacterSideways(targetCharacter))
        }
        if (!waitOnMoreQueuedActions){
            actionQueueProvider.runThroughActionQueue(finalAction = {})
            actionQueueProvider.clearQueue()
        }
    }

    fun zoomToFocusOnTile(){

    }

}

@Singleton
class ActionQueueProvider @Inject constructor(val actionRunner: ActionRunner) {
    private var actionQueue = ArrayList<ActorActionPair>()

    public fun runThroughActionQueue(finalAction: () -> Unit = {}){
        actionRunner.runThroughActionQueue(actionQueue, finalAction = finalAction)
    }

    public fun addAction(actorActionPair: ActorActionPair){
        actionQueue.add(actorActionPair)
    }

    public fun clearQueue(){
        actionQueue = ArrayList()
    }
}


