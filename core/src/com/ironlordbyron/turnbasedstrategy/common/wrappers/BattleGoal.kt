package com.ironlordbyron.turnbasedstrategy.common.wrappers

import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.tacMapState
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.missiongen.UnitSpawnParameter
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileProtoEntity


public interface BattleGoal{
    val name: String
    val description: String

    fun isGoalMet(): Boolean

    fun isFailed() : Boolean{
        return false
    }

    fun getGoalProgressString() : String {
        if (isFailed()) return "FAILED"
        return if (isGoalMet()) "COMPLETE" else "INCOMPLETE"
    }

    fun getRequiredZoneCreationParameters() : Collection<ZoneGenerationParameters>{
        return listOf()
    }
}

public data class ZoneGenerationParameters(val unitSpawnParams: Collection<TacMapUnitTemplate>,
                                           val tileEntityParams: Collection<TileProtoEntity<*>> = listOf())

public data class TileAndProtoEntity(val tileProtoEntity: TileProtoEntity<*>, val tileLocation: TileLocation)
/// Step one: map data is loaded and shoved into the map singleton.
/// for each battle goal, get a number of required zones (implies maps need a minimum number of zones to function)
/// Zone postprocessing step where we go through and modify the map to accommodate zone



