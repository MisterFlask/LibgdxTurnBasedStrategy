package com.ironlordbyron.turnbasedstrategy.common

import com.google.inject.Inject
import com.google.inject.Singleton
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttributeFactory
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
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
                                     val functionalCharacterAttributeFactory: FunctionalCharacterAttributeFactory,
                                     val tacticalMapState: TacticalMapState){

    fun getAttributes(thisCharacter: LogicalCharacter): List<FunctionalCharacterAttribute> {
        val functions = functionalCharacterAttributeFactory.getFunctionalAttributesForCharacter(thisCharacter)
        return functions
    }

    fun onDeath(thisCharacter: LogicalCharacter){
        functionalEffectRegistrar.runDeathEffects(thisCharacter)
        val attrs = getAttributes(thisCharacter)
        attrs.forEach{it.onDeath(thisCharacter)}
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
        val attrs = getAttributes(thisCharacter)
        attrs.forEach{it.onCharacterTurnStart(thisCharacter)}
    }

    fun onUnitCreation(thisCharacter: LogicalCharacter){
        val attrs = getAttributes(thisCharacter)
        attrs.forEach{it.onInitialization(thisCharacter)}
    }

    fun onTacMapInitialization(){
        for (character in tacticalMapState.listOfCharacters){
            onUnitCreation(character)
        }
    }

    fun afterApplicationOfAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute, stacksToApply: Int) {
        functionalEffectRegistrar.runOnApplicationEffects(logicalCharacter, logicalCharacterAttribute)//TODO
    }
}