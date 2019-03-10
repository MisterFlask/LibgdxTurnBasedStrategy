package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute


// These are IMMUTABLE OBJECTS
// mutable params should go in the LogicalAttribute.
public interface FunctionalUnitEffect<T>{
    val id: String // maps to the logical attribute given a unit.
    val clazz: Class<T>
    val stacks: Int
        get() = 1

    fun getMovementModifier(logicalAttr: T, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Int {
        return 0
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

}