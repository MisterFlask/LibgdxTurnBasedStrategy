package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbilityController @Inject constructor(val tacticalMapState: TacticalMapState,
                                            val mapHighlighter: MapHighlighter) {

    fun ActOnAbility(logicalAbility: LogicalAbility){
        if(logicalAbility.abilityClass == AbilityClass.TARGETED_ABILITY){
        }
    }
}
