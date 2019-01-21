package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.ai.PathfinderFactory
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttributeFactory
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.VisibleCharacterDataFactory
import com.ironlordbyron.turnbasedstrategy.controller.*
import com.ironlordbyron.turnbasedstrategy.view.CharacterSpriteUtils
import com.ironlordbyron.turnbasedstrategy.view.animation.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.*
import java.lang.IllegalStateException
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton




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
                                            val animationActionQueueProvider: AnimationActionQueueProvider,
                                            val revealActionGenerator: RevealActionGenerator,
                                            val visibleCharacterDataFactory: VisibleCharacterDataFactory,
                                            val characterModificationAnimationGenerator: CharacterModificationAnimationGenerator,
                                            val pathfinderFactory: PathfinderFactory,
                                            val pulseAnimationGenerator: PulseAnimationGenerator,
                                            val functionalCharacterAttributeFactory: FunctionalCharacterAttributeFactory) : EventListener{


    override fun consumeGuiEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.FinishedEnemyTurn -> {
                startPlayerTurn()
            }
        }
    }

    private fun startPlayerTurn() {
        for (unit in boardState.listOfCharacters){
            unit.actionsLeft = unit.maxActionsLeft
            if (!unit.isDead){
                characterSpriteUtils.brightenSprite(unit)
            }
            val functionalAttributes = unit.attributes.flatMap{functionalCharacterAttributeFactory.getFunctionalAttributesFromLogicalAttribute(it, unit)}
            for (attr in functionalAttributes){
                attr.onCharacterTurnStart(unit)
            }
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
            val pulseActionPair = pulseAnimationGenerator.generateActorActionPair(character.actor.characterActor, 1f)
            actorActionPairForHighlights.secondaryActions += pulseActionPair
            animationActionQueueProvider.addAction(actorActionPairForHighlights)
        }

        val result = getCharacterMovementActorActionPair(toTile, character)
        boardState.moveCharacterToTile(character, toTile)
        animationActionQueueProvider.addActions(result)
        if (character.endedTurn){
            animationActionQueueProvider.addAction(SpriteColorActorAction.build(character, SpriteColorActorAction.DIM_COLOR))
        }
        if (!waitOnMoreQueuedActions){
            animationActionQueueProvider.runThroughActionQueue(finalAction = {})
            animationActionQueueProvider.clearQueue()
        }

        // now mark the character as moved by darkening the sprite.
    }

    val TIME_TO_MOVE = .5f
    //TODO: Migrate this to an animation generator
    private fun getCharacterMovementActorActionPair(toTile: TileLocation,
                                                    character: LogicalCharacter,
                                                    breadcrumbHint: List<TileLocation>? = null) : List<ActorActionPair> {
        val breadcrumbs = breadcrumbHint?:getBreadcrumbs(character, toTile)
        val actorActionPairs = ArrayList<ActorActionPair>()
        val timePerSquare = TIME_TO_MOVE/breadcrumbs.size
        for (breadcrumb in breadcrumbs){
            val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(breadcrumb)
            var moveAction: Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), timePerSquare)
            actorActionPairs.add(ActorActionPair(character.actor, moveAction))
        }
        return actorActionPairs

    }

    private fun getBreadcrumbs(logicalCharacter: LogicalCharacter,
                               toTile: TileLocation): List<TileLocation> {
        val pathfinder = pathfinderFactory.createGridGraph(logicalCharacter)
        val tiles = pathfinder.acquireBestPathTo(
                logicalCharacter,
                toTile,
                allowEndingOnLastTile = true)
        return tiles?.map{it.location}?.toList() ?: throw IllegalStateException("Required to call this on a character that can go to the provided tile")
    }

    fun removeCharacter(character: LogicalCharacter) {
        boardState.listOfCharacters.remove(character)
        character.actor.remove()
    }


}


