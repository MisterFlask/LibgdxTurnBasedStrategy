package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation

class AbilityEffectFactory {

}


interface Ability{
    abstract fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?,
                               sourceCharacter: LogicalCharacter)

    abstract fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?,
                                 sourceCharacter: LogicalCharacter)

    abstract fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter)
}


class AttackAbility : Ability{

}