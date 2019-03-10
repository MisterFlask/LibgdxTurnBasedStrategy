package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import javax.inject.Singleton

@Singleton
@Autoinjectable
class SlimedUnitFunctionalEffect() : FunctionalUnitEffect<SlimedUnitLogicalEffect>{
    override val id: String = "SLIMED"
    override val clazz: Class<SlimedUnitLogicalEffect> = SlimedUnitLogicalEffect::class.java

    override fun getMovementModifier(logicalAttr: SlimedUnitLogicalEffect, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Int {
        return -1 * logicalAttr.stacks
    }

}

data class SlimedUnitLogicalEffect(var stacks: Int) : LogicalUnitEffect{
    override fun toEntry(): Pair<String, Any> {
        return "SLIMED" to this
    }
}