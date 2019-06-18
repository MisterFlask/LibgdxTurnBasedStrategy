package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Autoinjectable
class SlimedUnitFunctionalEffect @Inject constructor() : FunctionalUnitEffect() {
    override fun getMovementModifier(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Int {
        return -1 * logicalCharacterAttribute.stacks
    }
}