package com.ironlordbyron.turnbasedstrategy.rules

import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTileTracker


public class GameRules @Inject constructor(val logicalTileTracker: LogicalTileTracker,
                                           val boardState: TacticalMapState){
    fun canWalkOnTile(logicalCharacter: LogicalCharacter, tileLocation: TileLocation): Boolean{
        val terrain = logicalTileTracker.getLogicalTileFromLocation(tileLocation)!!.terrainType
        return logicalCharacter.tacMapUnit.walkableTerrainTypes.contains(terrain)
    }

}