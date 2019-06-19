package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptInput
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

    open fun getMovementModifier( params: FunctionalEffectParameters): Int {
        return 0
    }

    open fun onBeingStruck(params: FunctionalEffectParameters){

    }

    /**
     * Function gets run when the unit effect is added (just before, so it won't appear on the character).
     */
    open fun beforeApplication(params: FunctionalEffectParameters){

    }

    /**
     * Function gets run just after the unit effect is added.
     */
    open fun afterApplication(params: FunctionalEffectParameters){

    }


    open fun onDeath(params: FunctionalEffectParameters){

    }

    open fun onStrikingEnemy(params: FunctionalEffectParameters){

    }

    open fun onTurnStart(params: FunctionalEffectParameters){

    }

    open fun attemptToDamage(damageAttemptInput: DamageAttemptInput): DamageAttemptInput{
        return damageAttemptInput
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


data class FunctionalEffectParameters(val thisCharacter: LogicalCharacter,
                                 val logicalCharacterAttribute: LogicalCharacterAttribute,
                                 val stacks :Int)
data class LogicalAttributeRemovedEvent(val attribute: LogicalCharacterAttribute, val thisCharacter: LogicalCharacter) : TacticalGuiEvent()