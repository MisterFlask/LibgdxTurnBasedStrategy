package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider

/**
 * Attempts to move to closest enemy.
 * Will find the best path to that enemy.
 * Will move to closest usable tile.
 */
public class BasicEnemyAi(val tileMapOperationsHandler: TileMapOperationsHandler,
                          val tacticalMapState: TacticalMapState,
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

        val moverate = thisCharacter.tacMapUnit.movesPerTurn
        if (pathToEnemy.size > moverate - 2){
            return pathToEnemy.toList()[moverate - 2].location
        }

        return pathToEnemy.last().location
        // TODO: Ensure tile isn't occupied by enemy (if it is, stop one short)
    }

}