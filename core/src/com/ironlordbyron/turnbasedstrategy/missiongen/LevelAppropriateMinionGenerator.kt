package com.ironlordbyron.turnbasedstrategy.missiongen

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.campaign.battlegoals.fromId

public class LevelAppropriateMinionGenerator{
    val unitTemplateIds = listOf("WEAK_SLIME")
    fun getGenericMinions(numMinions: Int) : Collection<TacMapUnitTemplate> {
        return unitTemplateIds
                .repeat(10)
                .shuffled()
                .take(numMinions)
                .map{TacMapUnitTemplate.fromId(it)}
    }
}