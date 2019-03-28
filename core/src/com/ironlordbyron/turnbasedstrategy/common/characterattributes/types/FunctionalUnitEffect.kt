package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent


// These are IMMUTABLE OBJECTS
// mutable params should go in the LogicalAttribute.
public interface FunctionalUnitEffect<T>{
    val eventNotifier: EventNotifier
    val id: String // maps to the logical attribute given a unit.
    val clazz: Class<T>
    val stopsUnitFromActing: Boolean
        get() = false

    fun getMovementModifier(logicalAttr: T, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Int {
        return 0
    }

    fun onBeingStruck(logicalAttr: T,
                      thisCharacter: LogicalCharacter,
                      logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    /**
     * Function gets run when the unit effect is added (just before, so it won't appear on the character).
     */
    fun beforeApplication(logicalAttr: T, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    /**
     * Function gets run just after the unit effect is added.
     */
    fun afterApplication(logicalAttr: T, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    fun retrieveLogicalAttributesFromAttrMap(map: Map<String, Any>) : T?{
        val logicalAttribute = map[id] as T
        return logicalAttribute
    }

    fun onDeath(logicalAttr: T, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    fun onStrikingEnemy(logicalAttr: T, thisCharacter: LogicalCharacter, struckCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    fun onTurnStart(logicalAttr: T, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){

    }

    fun serializeUnitAttribute(attr: T): String? {
        return ObjectMapper().writeValueAsString(attr)
    }
    fun fromString(attr: String): T {
        return ObjectMapper().readValue(attr, clazz)
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