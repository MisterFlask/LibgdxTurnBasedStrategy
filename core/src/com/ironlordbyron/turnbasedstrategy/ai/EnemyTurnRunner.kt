package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttributeFactory
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.MapHighlighter
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.CharacterSpriteUtils
import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner
import com.ironlordbyron.turnbasedstrategy.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.SpriteActorFactory
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class EnemyTurnRunner @Inject constructor(val tiledMapOperationsHandler: TiledMapOperationsHandler,
                                                 val tileMapProvider: TileMapProvider,
                                                 val characterImageManager: CharacterImageManager,
                                                 val eventNotifier: EventNotifier,
                                                 val logicalTileTracker: LogicalTileTracker,
                                                 val imageActorFactory: SpriteActorFactory,
                                                 val boardState: TacticalMapState,
                                                 val enemyAiFactory:EnemyAiFactory,
                                                 val actionRunner: ActionRunner,
                                                 val characterSpriteUtils: CharacterSpriteUtils,
                                                 val mapHighlighter: MapHighlighter,
                                                 val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                 val gameBoardOperator: GameBoardOperator,
                                                 val animationActionQueueProvider: AnimationActionQueueProvider,
                                                 val abilityFactory: AbilityFactory,
                                                 val logicHooks: LogicHooks){

    public fun endTurn() {
        runEnemyTurn()
    }

    public fun runEnemyTurn() {
        animationActionQueueProvider.clearQueue()
        logicHooks.onEnemyTurnStart()
        for (enemyCharacter in boardState.listOfEnemyCharacters) {

            if (enemyCharacter.endedTurn){
                continue //characters that have already gone don't get turns
            }

            if (enemyCharacter.isDead){
                continue //dead characters don't get turns
            }

            val ai = enemyAiFactory.getEnemyAi(enemyCharacter.tacMapUnit.enemyAiType)
            val nextActions = ai.getNextActions(enemyCharacter);
            for (action in nextActions){
                when(action){
                    is AiPlannedAction.MoveToTile -> gameBoardOperator.moveCharacterToTile(enemyCharacter,
                            action.to,
                            waitOnMoreQueuedActions = true,
                            wasPlayerInitiated = false)
                    is AiPlannedAction.AbilityUsage ->  {
                        val charToTarget = boardState.characterAt(action.squareToTarget)
                        val ability = abilityFactory.acquireAbility(action.ability)
                        ability.activateAbility(action.squareToTarget, charToTarget, action.sourceCharacter, equipment = action.ability.equipment)
                    }
                }
            }
        }
        animationActionQueueProvider.runThroughActionQueue(finalAction = {
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.FinishedEnemyTurn())
        })
        animationActionQueueProvider.clearQueue()
    }
}