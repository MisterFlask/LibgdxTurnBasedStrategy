package com.ironlordbyron.turnbasedstrategy.ai.goals

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.ai.*
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.distanceTo
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

val MIN_ATTACK_DISTANCE = 10
class ConquerCityGoal(): Goal{

    override fun describe(): String {
        return "Goal:Conquer"
    }
    val basicAiDecisions: BasicAiDecisions by lazy{
        GameModuleInjector.generateInstance(BasicAiDecisions::class.java)
    }
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }

    override fun formulateIntent(thisCharacter: LogicalCharacter) : Intent{
        if (thisCharacter.tacMapUnit.enemyAiType == EnemyAiType.BASIC){
            val shouldFindEnemyToAttack = shouldAttackClosestEnemy(thisCharacter)
            if (shouldFindEnemyToAttack) {
                val attackOption = basicAiDecisions.getAttackIntentForThisTurn(thisCharacter)
                if (attackOption is Intent.Attack){
                    return attackOption
                } else{
                    return Intent.Move()
                }
            } else{
                return Intent.Move()
            }

        }
        return Intent.Other()
    }

    fun shouldAttackClosestEnemy(thisCharacter: LogicalCharacter): Boolean{
        val closestEnemy = tacticalMapState.closestPlayerControlledCharacterTo(thisCharacter)
        val shouldFindEnemyToAttack = closestEnemy != null && closestEnemy.tileLocation.distanceTo(thisCharacter.tileLocation) < MIN_ATTACK_DISTANCE
        return shouldFindEnemyToAttack
    }

    override fun executeOnIntent(thisCharacter: LogicalCharacter): List<AiPlannedAction> {

        if (thisCharacter.intent is Intent.None){
            thisCharacter.intent = formulateIntent(thisCharacter)
        } //TODO: This should be done after spawning
        when(thisCharacter.intent.intentType){
            IntentType.ATTACK -> {
                Logging.DebugCombatLogic("Character ${thisCharacter.tacMapUnit.templateName} is attempting attack")
                val attackIntent = thisCharacter.intent as Intent.Attack
                if (basicAiDecisions.canTargetCharacterWithAbility(thisCharacter, tacticalMapState.getCharacterFromId(attackIntent.logicalCharacterUuid), thisCharacter.intent.intentType)){
                    val plannedActions = basicAiDecisions.getNecessaryMoveForTargetingCharacterWithAbility(thisCharacter, tacticalMapState.getCharacterFromId(attackIntent.logicalCharacterUuid), thisCharacter.intent.intentType)!!
                    return plannedActions
                }
                // TODO: This should be done later, after movement
                thisCharacter.intent = formulateIntent(thisCharacter)
                return executeOnIntent(thisCharacter)
            }
            IntentType.MOVE -> {
                return basicAiDecisions.beelineTowardNearestUnownedCity(thisCharacter)
            }
            IntentType.NONE, IntentType.DEFEND, IntentType.OTHER  -> {
                println("Not supported: ${thisCharacter.intent.intentType}" )
                return listOf()
            }
        }
    }

}