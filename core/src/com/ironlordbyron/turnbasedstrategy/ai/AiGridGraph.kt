package com.ironlordbyron.turnbasedstrategy.ai

import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.rules.GameRules
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTile
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTileTracker
import org.xguzm.pathfinding.grid.NavigationGrid
import org.xguzm.pathfinding.grid.GridCell
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import java.lang.IllegalArgumentException

private val SENTINEL_VALUE = 100000

public class AiGridGraphFactory @Inject constructor(val tileTracker: LogicalTileTracker,
                                                    val tacticalMapState: TacticalMapState,
                                                    val gameRules: GameRules){

    public fun createGridGraph(logicalCharacter: LogicalCharacter) : AiGridGraph{
        return AiGridGraph(tileTracker,
                tacticalMapState,
                 gameRules, logicalCharacter)
    }
}

public class AiGridGraph (val tileTracker: LogicalTileTracker,
                         val tacticalMapState: TacticalMapState,
                          val gameRules: GameRules,
                          val logicalCharacter: LogicalCharacter) {

    val navigationGrid = NavigationGrid<PathfindingTileLocation>();
    init{
        navigationGrid.setNodes(convertToNodes())
    }

    private fun convertToNodes(): Array<out Array<PathfindingTileLocation>>? {
        val tileArray = Array<Array<PathfindingTileLocation>>(tileTracker.width(), {Array<PathfindingTileLocation>(tileTracker.height(), {PathfindingTileLocation()})});
        tileArray.forEachIndexed{
            x, tileRow -> tileRow.forEachIndexed {

             y, pathingTile ->
                pathingTile.location = TileLocation(x,y)
                pathingTile.x = x
                pathingTile.y = y
                pathingTile.isWalkable = gameRules.canWalkOnTile(logicalCharacter, pathingTile.location)
            }
        }
        return tileArray
    }

    public fun acquireClosestEnemy(logicalCharacter : LogicalCharacter): LogicalCharacter? {
        if (logicalCharacter.playerControlled){
            throw IllegalArgumentException("Cannot find closest enemy of player character (unsupported). ")
        }
        val closestEnemyCharacter = tacticalMapState.listOfCharacters.filter{
            tacMapUnit -> tacMapUnit.playerControlled
        }.minBy{tacMapUnit -> acquireBestPathTo(logicalCharacter, endLocation = tacMapUnit.tileLocation)?.size?:SENTINEL_VALUE}

        if (closestEnemyCharacter == null){
            return null
        }
        if (acquireBestPathTo(logicalCharacter, endLocation = closestEnemyCharacter.tileLocation) == null){
            return null
        }

        return closestEnemyCharacter
    }

    public fun acquireBestPathTo(startCharacter: LogicalCharacter, endLocation: TileLocation) : Collection<PathfindingTileLocation>?{
        var opt = GridFinderOptions()
        opt.allowDiagonal = false
        var finder = AStarGridFinder(PathfindingTileLocation::class.java, opt)
        // NOTE:  The navigation grid REQUIRES UTTERLY that you reset nodes in between runs.
        navigationGrid.setNodes(convertToNodes())
        val bestPath = finder.findPath(startCharacter.tileLocation.x,
                startCharacter.tileLocation.y, endLocation.x,
                endLocation.y, this.navigationGrid)

        println("${if (bestPath == null) "FAILURE" else "SUCCESS"} in finding route between ${startCharacter.tileLocation} and ${endLocation}")

        if (bestPath?.size?:0 <= 1){
            return listOf()
        }
        return bestPath.subList(0, bestPath.size -1)
    }
}