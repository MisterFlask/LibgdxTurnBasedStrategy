package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptResult
import com.ironlordbyron.turnbasedstrategy.common.LogicalAbilityAndEquipment
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.DamageType
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

public abstract class FunctionalAttributeEffect{
    val eventNotifier: EventNotifier by lazy {
        GameModuleInjector.generateInstance(EventNotifier::class.java)
    }
    open val stopsUnitFromActing: Boolean
        get() = false

    open fun getMovementModifier( thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Int {
        return 0
    }

    open fun onBeingStruck(
                      thisCharacter: LogicalCharacter,
                      logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    /**
     * Function gets run when the unit effect is added (just before, so it won't appear on the character).
     */
    open fun beforeApplication(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    /**
     * Function gets run just after the unit effect is added.
     */
    open fun afterApplication(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }


    open fun onDeath(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    open fun onStrikingEnemy(thisCharacter: LogicalCharacter, struckCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    open fun onTurnStart(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    open fun serializeUnitAttribute(attr: LogicalCharacterAttribute): String? {
        return ObjectMapper().writeValueAsString(attr)
    }

    open fun attemptToDamage(thisCharacter: LogicalCharacter,
                             logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                             damageType: DamageType,
                             damage: Int): DamageAttemptResult{
        return DamageAttemptResult(damage, thisCharacter, thisCharacter, damage)
    }

    ////////////////////////////////////////////////////////
    /// UTILITY FUNCTIONS BELOW THIS LINE //////////////////
    ////////////////////////////////////////////////////////

    fun removeThisAttribute(logicalAttributeId: String, thisCharacter: LogicalCharacter){
        val attrRemoved = thisCharacter.tacMapUnit.attributes.filter{it.id == logicalAttributeId }.first()
        thisCharacter.tacMapUnit.attributes.removeIf { it.id == logicalAttributeId }
        eventNotifier.notifyListenersOfGuiEvent(LogicalAttributeRemovedEvent(attrRemoved, thisCharacter))
    }
}

data class LogicalAttributeRemovedEvent(val attribute: LogicalCharacterAttribute, val thisCharacter: LogicalCharacter) : TacticalGuiEvent()