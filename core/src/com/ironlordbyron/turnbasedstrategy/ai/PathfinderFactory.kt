package com.ironlordbyron.turnbasedstrategy.ai

import com.google.inject.ImplementedBy
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter

@ImplementedBy(BespokePathfinderFactory::class)
interface PathfinderFactory {
    public fun createGridGraph(logicalCharacter: LogicalCharacter) : Pathfinder
}


class BespokePathfinderFactory : PathfinderFactory{
    val pathfinder = BespokePathfinder()
    override fun createGridGraph(logicalCharacter: LogicalCharacter): Pathfinder {
        return pathfinder
    }

}