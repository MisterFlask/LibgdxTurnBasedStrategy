package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.ai.PathfinderFactory
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.VisibleCharacterDataFactory
import com.ironlordbyron.turnbasedstrategy.controller.*
import com.ironlordbyron.turnbasedstrategy.tacmapunits.actionManager
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
                                            val logicHooks: LogicHooks) : EventListener{


    override fun consumeGuiEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.FinishedEnemyTurn -> {
                startPlayerTurn()
            }
        }
    }

    private fun startPlayerTurn() {
        logicHooks.onPlayerTurnStart()
        for (unit in boardState.listOfCharacters){
            unit.actionsLeft = unit.maxActionsLeft
            if (!unit.isDead){
                characterSpriteUtils.brightenSprite(unit)
            }
            eventNotifier.notifyListenersOfGameEvent(TacticalGameEvent.UnitTurnStart(unit))
        }
        // now, run the animations
        animationActionQueueProvider.runThroughActionQueue()
    }

    init{
        eventNotifier.registerGuiListener(this)
    }


    // moves the character to the given tile logically, and returns the clickListeningActor/action pair for animation purposes.
    fun moveCharacterToTile(character: LogicalCharacter, toTile: TileLocation, waitOnMoreQueuedActions: Boolean,
                            wasPlayerInitiated: Boolean){
        actionManager.moveCharacterToTile(character, toTile, waitOnMoreQueuedActions, wasPlayerInitiated)
    }

    fun removeCharacter(character: LogicalCharacter) {
        boardState.listOfCharacters.remove(character)
        character.actor.remove()

    }


}


