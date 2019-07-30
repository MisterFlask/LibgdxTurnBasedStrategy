package com.ironlordbyron.turnbasedstrategy.ai.goals

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.ai.*
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

class AttackGoal() : Goal{

    val basicAiDecisions: BasicAiDecisions by lazy{
        GameModuleInjector.generateInstance(BasicAiDecisions::class.java)
    }
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    override fun executeOnIntent(thisCharacter: LogicalCharacter): List<AiPlannedAction> {
        if (thisCharacter.intent.intentType == IntentType.NONE){
            thisCharacter.intent = Intent.Move()
        }

        when(thisCharacter.intent) {
            is Intent.Move -> {
                Logging.DebugCombatLogic("Character ${thisCharacter.tacMapUnit.templateName} is attempting movement")

                val bestPathToClosestPlayerUnit = basicAiDecisions.pathfindToClosestPlayerUnit(thisCharacter)
                if (bestPathToClosestPlayerUnit == null) {
                    println("Could not find best path to tile ")
                    return listOf()
                }
                if (bestPathToClosestPlayerUnit.size == 0) {
                    return listOf()
                }
                val nextMove = AiPlannedAction.MoveToTile(bestPathToClosestPlayerUnit.last().location)
                return listOf(nextMove)
            }
            is Intent.Attack -> {
                Logging.DebugCombatLogic("Character ${thisCharacter.tacMapUnit.templateName} is attempting attack")
                val attackIntent = thisCharacter.intent as Intent.Attack
                if (basicAiDecisions.canTargetCharacterWithAbility(thisCharacter, tacticalMapState.getCharacterFromId(attackIntent.logicalCharacterUuid), thisCharacter.intent.intentType)) {
                    val plannedActions = basicAiDecisions.getNecessaryMoveForTargetingCharacterWithAbility(thisCharacter, tacticalMapState.getCharacterFromId(attackIntent.logicalCharacterUuid), thisCharacter.intent.intentType)!!
                    return plannedActions
                }
                thisCharacter.intent = formulateIntent(thisCharacter)
                return executeOnIntent(thisCharacter)
            }
        }

        Logging.DebugCombatLogic("Couldn't figure out intent!")
        return listOf()
    }

    override fun formulateIntent(thisCharacter: LogicalCharacter) : Intent{
        if (thisCharacter.tacMapUnit.enemyAiType == EnemyAiType.BASIC){
            return basicAiDecisions.getAttackIntentForThisTurn(thisCharacter)
        }
        return Intent.Move()
    }
}