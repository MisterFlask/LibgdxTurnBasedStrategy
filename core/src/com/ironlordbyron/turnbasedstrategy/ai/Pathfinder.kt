package com.ironlordbyron.turnbasedstrategy.ai

import com.google.inject.ImplementedBy
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation

@ImplementedBy(AiGridGraph::class)
interface Pathfinder {
    fun acquireBestPathTo(character: LogicalCharacter, endLocation: TileLocation, allowEndingOnLastTile: Boolean,
                          allowFuzzyMatching:Boolean = true,restrictToCharacterMoveRange: Boolean = false) : Collection<PathfindingTileLocation>?
}
