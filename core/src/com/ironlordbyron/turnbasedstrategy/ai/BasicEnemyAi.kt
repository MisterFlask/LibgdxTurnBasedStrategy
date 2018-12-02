package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider

public class BasicEnemyAi(val tileMapOperationsHandler: TileMapOperationsHandler,
                          val tileMapProvider: TileMapProvider,
                          val aiGridGraphFactory: AiGridGraphFactory) : EnemyAi{
    override fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction> {
        val nextMove = getNextMoveLocation(thisCharacter)
        if (nextMove == null){
            return listOf() // todo: add attacks
        }
        return listOf(AiPlannedAction.MoveToTile(nextMove))
    }

    fun getNextMoveLocation(thisCharacter: LogicalCharacter) : TileLocation?{
        val map = tileMapProvider.tiledMap
        val aiGridGraph = aiGridGraphFactory.createGridGraph(thisCharacter)

        val closestEnemy = aiGridGraph.acquireClosestEnemy(thisCharacter)
        if (closestEnemy == null){
            return null
        }

        // TODO; this doesn't exactly work how i want, but it's a low priority for fixing.
        val targetEndTile = aiGridGraph.findClosestUnoccupiedTileTo(closestEnemy.tileLocation, allowEndingOnLastTile = false)
        if (targetEndTile == null){
            println("WARNING: Could not acquire target tile for ai.")
            return null
        }

        val pathToEnemy = aiGridGraph.acquireBestPathTo(thisCharacter, targetEndTile,
                allowEndingOnLastTile = true)

        if (pathToEnemy == null){
            return null
        }
        if (pathToEnemy.isEmpty()){
            return null
        }
        return pathToEnemy.last().location
        // TODO: Ensure tile isn't occupied by enemy (if it is, stop one short)
    }

}