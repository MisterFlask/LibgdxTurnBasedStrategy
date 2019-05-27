package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import javax.inject.Inject

public class ContextualAbilityFactory @Inject constructor (val logicalTileTracker: LogicalTileTracker,
                                                           val tacticalMapAlgorithms: TacticalMapAlgorithms){


    fun isContextualAbilityValidForUse(contextualAbilityParams: ContextualAbilityParams,
                                       sourceCharacter: LogicalCharacter) : Boolean{
        if (contextualAbilityParams.requiresDoorNearby){

        }

        return true
    }

    fun getContextualAbilitiesAvailableForCharacter(sourceCharacter: LogicalCharacter): Collection<LogicalAbility> {
        return ContextualAbilities.allContextualAbilities
                .filter{it.requirement != null}
                .filter{it.requirement!!.canUseAbility(sourceCharacter)}
    }


}