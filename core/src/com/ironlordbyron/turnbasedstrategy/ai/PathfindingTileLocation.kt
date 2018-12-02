package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import org.xguzm.pathfinding.grid.GridCell

class PathfindingTileLocation() : GridCell() {
    public lateinit var location: TileLocation

    // equals implementation required
    override fun equals(other: Any?): Boolean {
        if (!(other is PathfindingTileLocation)){
            return false
        }
        return location.equals(other.location)
    }
}
