package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActor

/**
 * Represents a mutable character generated from a template.
 * Has a location, an associated actor, and a
 */
data class LogicalCharacter(val actor: SpriteActor, var tileLocation: TileLocation, val tacMapUnit: TacMapUnitTemplate)

data class TileLocation(val x: Int, val y: Int)
