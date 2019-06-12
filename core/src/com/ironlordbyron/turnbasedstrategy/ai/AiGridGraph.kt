package com.ironlordbyron.turnbasedstrategy.ai

import com.google.common.base.Stopwatch
import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import org.xguzm.pathfinding.grid.NavigationGrid
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

private val SENTINEL_VALUE = 100000

public class AiGridGraphFactory @Inject constructor(val tileTracker: LogicalTileTracker,
                                                    val tacticalMapState: TacticalMapState,
                                                    val tacticalMapAlgorithms: TacticalMapAlgorithms) : PathfinderFactory{

    override public fun createGridGraph(logicalCharacter: LogicalCharacter) : AiGridGraph{
        return AiGridGraph(tileTracker,
                tacticalMapState, logicalCharacter, tacticalMapAlgorithms)
    }
}

public class AiGridGraph (val tileTracker: LogicalTileTracker,
                          val tacticalMapState: TacticalMapState,
                          val logicalCharacter: LogicalCharacter,
                          val tacticalMapAlgorithms: TacticalMapAlgorithms) : Pathfinder {

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
                pathingTile.isWalkable = tacticalMapAlgorithms.canWalkOnTile(logicalCharacter, pathingTile.location)
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
        }.minBy{tacMapUnit -> acquireBestPathTo(logicalCharacter, endLocation = tacMapUnit.tileLocation, allowEndingOnLastTile = false)?.size?:SENTINEL_VALUE}

        if (closestEnemyCharacter == null){
            return null
        }
        if (acquireBestPathTo(logicalCharacter, endLocation = closestEnemyCharacter.tileLocation, allowEndingOnLastTile = false) == null){
            return null
        }

        return closestEnemyCharacter
    }

    val MAX_RADIUS = 5
    public fun findClosestUnoccupiedTileTo(origin: TileLocation, allowEndingOnLastTile: Boolean) : TileLocation?{
        // look at a max radius of five, then give up
        val tileSet = HashSet<TileLocation>()
        tileSet.add(origin)
        for (i in 0 .. 5){
            val nextRing = tileSet.flatMap { getNeighbors(it) }
            tileSet.addAll(nextRing)
            // todo: clumsy implementation
            for (tile in nextRing){
                if (tile == origin && !allowEndingOnLastTile){
                    continue
                }
                if (tacticalMapAlgorithms.isTileUnoccupied(tileLocation = tile)){
                    return tile
                }
            }
        }
        return null
    }

    private fun getNeighbors(tileLocation: TileLocation) : Collection<TileLocation>{
        return listOf(tileLocation.copy(x = tileLocation.x + 1),
                tileLocation.copy(y = tileLocation.y + 1),
                tileLocation.copy(x = tileLocation.x - 1),
                tileLocation.copy(y = tileLocation.y - 1))
                .filter{tileTracker.getLogicalTileFromLocation(it) != null}
    }


    override public fun acquireBestPathTo(startCharacter: LogicalCharacter, endLocation: TileLocation, allowEndingOnLastTile: Boolean) : Collection<PathfindingTileLocation>?{

        val stopwatch = Stopwatch.createStarted()
        try {
            var opt = GridFinderOptions()
            opt.allowDiagonal = false
            // navigationGrid.setNodes(convertToNodes())
            var finder = AStarGridFinder(PathfindingTileLocation::class.java, opt)
            val bestPath = finder.findPath(startCharacter.tileLocation.x,
                    startCharacter.tileLocation.y,
                    endLocation.x,
                    endLocation.y,
                    this.navigationGrid)


            // println("${if (bestPath == null) "FAILURE" else "SUCCESS"} in finding route between ${startCharacter.tileLocation} and ${endLocation}")

            if (!allowEndingOnLastTile) {
                if (bestPath?.size ?: 0 <= 1) {
                    return listOf()
                }
                val answer = bestPath.subList(0, bestPath.size - 1)
                return answer
            } else {
                return bestPath
            }
        } catch(e: Exception){
            println("ERROR when attempting to go from ${startCharacter.tileLocation} to ${endLocation} with allowEndingOnLastTile=${allowEndingOnLastTile}")
            throw e;
        } finally{
            stopwatch.stop()
            // println("Millis elapsed for pathfinding:" + stopwatch.elapsed(TimeUnit.MILLISECONDS))
        }
    }
}