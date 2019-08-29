package com.ironlordbyron.turnbasedstrategy.common.campaign

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import javax.inject.Singleton

@Singleton
class CharacterRoster{
    val characters = ArrayList<TacMapUnitTemplate>()

    init {
        //todo: better

        characters.addAll(listOf(TacMapUnitTemplate.RANGED_ENEMY, TacMapUnitTemplate.DEFAULT_UNIT))
    }
}