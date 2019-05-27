package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation

public interface ContextualAbilityRequirement{
    fun canUseAbility(characterUsing: LogicalCharacter) : Boolean

}