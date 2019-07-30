package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.MapHighlighter
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.CharacterSpriteUtils
import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner
import com.ironlordbyron.turnbasedstrategy.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.SpriteActorFactory
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tileentity.CityTileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimationSpeedManager
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.PulseAnimationGenerator
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
                                                 val logicHooks: LogicHooks,
                                                 val pulseAnimationGenerator: PulseAnimationGenerator){

    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }

    public fun endTurn() {
        runEnemyTurn()
    }

    public fun runEnemyTurn() {
        animationActionQueueProvider.clearQueue()
        logicHooks.onEnemyTurnStart()
        val enemies = boardState.listOfEnemyCharacters
        for (enemyCharacter in enemies) {
            if (!logicHooks.canUnitAct(enemyCharacter)){
                continue // characters that can't act don't get turns
            }

            if (enemyCharacter.tacMapUnit.turnStartAction != null){
                enemyCharacter.tacMapUnit.turnStartAction.performAction(enemyCharacter)
            }

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
                        val ability = action.ability.ability.abilityTargetingParameters
                        performTileHighlightAnimationForAction(enemyCharacter, action)
                        ability.activateAbility(action.squareToTarget, charToTarget, action.sourceCharacter, action.ability)
                    }
                }
            }
            conquerCitiesStep(enemyCharacter)
        }
        animationActionQueueProvider.runThroughActionQueue(finalAction = {
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.FinishedEnemyTurn())
        })
        animationActionQueueProvider.clearQueue()
    }

    public fun conquerCitiesStep(enemyCharacter: LogicalCharacter){
        val tileEntityAtLocation = enemyCharacter.tileLocation.entity()
        if (tileEntityAtLocation != null && tileEntityAtLocation is CityTileEntity){
            actionManager.conquerCityAction("City conquered", tileEntityAtLocation)
        }
    }

    private fun performTileHighlightAnimationForAction(enemyCharacter: LogicalCharacter, action: AiPlannedAction.AbilityUsage) {
        val tilesThatActionCanTarget = action.ability.getSquaresInRangeOfAbility(enemyCharacter.tileLocation, enemyCharacter)
        val actorActionPairForHighlights = mapHighlighter.getTileHighlightActorActionPairs(tilesThatActionCanTarget, HighlightType.ENEMY_ATTACK_TILE, enemyCharacter.actor.characterActor)
        val pulseActionPair = pulseAnimationGenerator.generateActorActionPair(enemyCharacter.actor.characterActor, 1f / AnimationSpeedManager.animationSpeedScale)
        actorActionPairForHighlights.secondaryActions += pulseActionPair
        animationActionQueueProvider.addAction(actorActionPairForHighlights)
    }
}