package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

interface LogicalUnitEffect{
    fun toEntry() : Pair<String, Any>
}