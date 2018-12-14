package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TemporaryAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.MapHighlighter
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.CharacterSpriteUtils
import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActorFactory
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class EnemyTurnRunner @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
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
                             val gameBoardOperator: GameBoardOperator){

    public fun endTurn() {
        runEnemyTurn()
    }

    public fun runEnemyTurn() {
        gameBoardOperator.clearQueue()
        for (enemyCharacter in boardState.listOfEnemyCharacters) {
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
                        gameBoardOperator.damageCharacter(charToTarget, true)
                    }
                }
            }
        }
        actionRunner.runThroughActionQueue(gameBoardOperator.actionQueue, finalAction = {
            eventNotifier.notifyListeners(TacticalGuiEvent.FinishedEnemyTurn())
        })
        gameBoardOperator.clearQueue()
    }
}