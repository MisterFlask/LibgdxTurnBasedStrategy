package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActor
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TerrainType
import org.xguzm.pathfinding.grid.NavigationGridGraphNode

/**
 * Represents a mutable character generated from a template.
 * Has a location, an associated actor, and a
 */
data class LogicalCharacter(val actor: SpriteActor,
                            var tileLocation: TileLocation,
                            val tacMapUnit: TacMapUnitTemplate,
                            val playerControlled: Boolean,
                            var movedThisTurn: Boolean = false){

}
