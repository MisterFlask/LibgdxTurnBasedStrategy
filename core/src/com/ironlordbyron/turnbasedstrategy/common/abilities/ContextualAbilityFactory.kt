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
            val neighbors = (logicalTileTracker.getNeighbors(sourceCharacter.tileLocation))
            if (neighbors.filter{logicalTileTracker.isDoor(it)}.isNotEmpty()){
                return true
            }else{
                return false
            }
        }

        return true
    }

    fun getContextualAbilitiesAvailableForCharacter(sourceCharacter: LogicalCharacter): Collection<LogicalAbility> {
        return ContextualAbilities.allContextualAbilities
                .filter{it.context != null}
                .filter{isContextualAbilityValidForUse(it.context!!, sourceCharacter)}
    }


}