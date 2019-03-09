package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter


// These are IMMUTABLE OBJECTS
// mutable params should go in the LogicalAttribute.
public interface FunctionalUnitEffect<T>{
    val id: String // maps to the logical attribute given a unit.
    val clazz: Class<T>

    fun retrieveLogicalAttributesFromAttrMap(map: Map<String, Any>) : T?{
        val logicalAttribute = map[id] as T
        return logicalAttribute
    }

    fun onDeath(logicalAttr: T, thisCharacter: LogicalCharacter){

    }

    fun onStrikingEnemy(logicalAttr: T, thisCharacter: LogicalCharacter, struckCharacter: LogicalCharacter){

    }

    fun onApplication(logicalAttr: T, thisCharacter: LogicalCharacter){

    }

    fun onTurnStart(logicalAttr: T, thisCharacter: LogicalCharacter){

    }

    fun serializeUnitAttribute(attr: T): String? {
        return ObjectMapper().writeValueAsString(attr)
    }
    fun fromString(attr: String): T {
        return ObjectMapper().readValue(attr, clazz)
    }

}