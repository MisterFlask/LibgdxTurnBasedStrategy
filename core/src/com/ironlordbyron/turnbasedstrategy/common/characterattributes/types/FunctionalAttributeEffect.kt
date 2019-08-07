package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptInput
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

public abstract class FunctionalAttributeEffect() {
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

    open fun applyDamageMods(damageAttemptInput: DamageAttemptInput, params: FunctionalEffectParameters): DamageAttemptInput{
        return damageAttemptInput
    }

    ////////////////////////////////////////////////////////
    /// UTILITY FUNCTIONS BELOW THIS LINE //////////////////
    ////////////////////////////////////////////////////////

    fun removeThisAttribute(logicalAttributeId: String, thisCharacter: LogicalCharacter){
        val attrRemoved = thisCharacter.tacMapUnit.getAttributes()
                .filter{it.logicalAttribute.id == logicalAttributeId }.first().logicalAttribute
        thisCharacter.tacMapUnit.removeAttributeById(logicalAttributeId)
        eventNotifier.notifyListenersOfGuiEvent(LogicalAttributeRemovedEvent(attrRemoved, thisCharacter))
    }

    open fun onInitialization(params: FunctionalEffectParameters){

    }
}


data class FunctionalEffectParameters(val thisCharacter: LogicalCharacter,
                                 val logicalCharacterAttribute: LogicalCharacterAttribute,
                                 val stacks :Int)
data class LogicalAttributeRemovedEvent(val attribute: LogicalCharacterAttribute, val thisCharacter: LogicalCharacter) : TacticalGuiEvent()