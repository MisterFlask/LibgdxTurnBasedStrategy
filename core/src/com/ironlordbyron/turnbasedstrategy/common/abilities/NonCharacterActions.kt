package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider

public class NonCharacterActions(val tileMapProvider:  TileMapProvider){

    fun getNonCharacterActionsAvailable() : Collection<LogicalAbility>{
        return listOf()
    }
}
