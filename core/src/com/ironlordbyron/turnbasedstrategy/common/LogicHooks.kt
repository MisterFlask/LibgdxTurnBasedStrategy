package com.ironlordbyron.turnbasedstrategy.common

import com.google.inject.Inject
import com.google.inject.Singleton
import com.ironlordbyron.turnbasedstrategy.ai.BasicAiDecisions
import com.ironlordbyron.turnbasedstrategy.ai.Intent
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.DamageType
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.UnitWasStruckEvent
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.entrypoints.FunctionalEffectRegistrar

/**
 *
/// This is a centralized storehouse of all the "logical hooks" (i.e. character attributes, etc)
/// that we want to be run in response to game events (e.g. characters starting their turns, or when they die)

 TURN ORDER
 1)  Activation phase
 2)  Run turn start effects
 3)  (player/enemy performs actions)
 4)  Run turn end effects
 */
@Singleton
class LogicHooks @Inject constructor(val functionalEffectRegistrar: FunctionalEffectRegistrar,
                                     val tacticalMapState: TacticalMapState,
                                     val eventNotifier: EventNotifier,
                                     val basicAiDecisions: BasicAiDecisions,
                                     val actionManager: ActionManager) : GameEventListener {
    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is UnitWasStruckEvent -> onCharacterWasStruck(tacticalGameEvent.targetCharacter)
        }
    }

    init{
        eventNotifier.registerGameListener(this)
    }

    fun onDeath(thisCharacter: LogicalCharacter){
        functionalEffectRegistrar.runDeathEffects(thisCharacter)
    }


    fun onPlayerTurnStart(){
        for (unit in tacticalMapState.listOfCharacters.filter{it.playerControlled}){
            if (unit.isDead){
                continue // we're ignoring dead units
            }
            onCharacterTurnStart(unit)
        }
    }

    public fun calculateAllowedUnitMovement(logicalCharacter: LogicalCharacter) : Int{
        return logicalCharacter.tacMapUnit.movesPerTurn + functionalEffectRegistrar.getMovementModifiers(logicalCharacter)
    }

    fun onEnemyTurnStart(){
        for (unit in tacticalMapState.listOfEnemyCharacters){
            onCharacterTurnStart(unit)
        }
    }

    fun onCharacterTurnStart(thisCharacter: LogicalCharacter){
        functionalEffectRegistrar.runTurnStartEffects(thisCharacter)
    }

    fun onUnitCreation(thisCharacter: LogicalCharacter){
        thisCharacter.getAttributes().forEach {
            logicalAttribute -> logicalAttribute.logicalAttribute.customEffects.forEach{
                customEffect -> customEffect.onInitialization(FunctionalEffectParameters(thisCharacter,
                logicalAttribute.logicalAttribute,
                logicalAttribute.stacks))
            }
        }
    }

    fun onTacMapInitialization(){
        for (character in tacticalMapState.listOfCharacters){
            onUnitCreation(character)
        }
    }

    fun canUnitAct(thisCharacter: LogicalCharacter): Boolean {
        return functionalEffectRegistrar.canUnitAct(thisCharacter)
    }

    fun afterApplicationOfAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute, stacksToApply: Int) {
        functionalEffectRegistrar.runOnApplicationEffects(logicalCharacter, logicalCharacterAttribute)//TODO
    }

    fun onCharacterWasStruck(targetCharacter: LogicalCharacter) {
        functionalEffectRegistrar.runAfterStruckCharacterEffects(targetCharacter)
    }

    fun attemptToDamage(damageAttemptInput: DamageAttemptInput): DamageAttemptInput {
        var damageAttemptResult = damageAttemptInput
        val victimEffectsToRun = damageAttemptInput.targetCharacter.getAttributes().flatMap{
            it.logicalAttribute.customEffects}
        for (effect in victimEffectsToRun){
            damageAttemptResult = effect.attemptToDamage(damageAttemptResult)
        }
        return damageAttemptResult
    }

    fun playerMovedCharacter(playerCharacter: LogicalCharacter){
        for (character in tacticalMapState.listOfEnemyCharacters){
            val intent = character.intent
            if (intent is Intent.Attack && intent.logicalCharacterUuid == playerCharacter.id){
                if(!basicAiDecisions.isIntentStillPossible(character)){
                    actionManager.risingText("?!?", character.tileLocation)
                    val newIntent = basicAiDecisions.formulateIntent(character)
                    character.intent = newIntent
                }
            }
        }
    }
}

public data class DamageAttemptInput(val sourceCharacter: LogicalCharacter,
                                     val targetCharacter: LogicalCharacter,
                                     val sourceAbility: LogicalAbilityAndEquipment,
                                     val damage:Int,
                                     val damageType: DamageType)