package com.ironlordbyron.turnbasedstrategy.ai

import com.google.inject.ImplementedBy
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter

@ImplementedBy(AiGridGraphFactory::class)
interface PathfinderFactory {
    public fun createGridGraph(logicalCharacter: LogicalCharacter) : AiGridGraph
}
