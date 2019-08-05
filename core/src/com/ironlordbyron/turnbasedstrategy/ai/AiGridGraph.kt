package com.ironlordbyron.turnbasedstrategy.ai

import com.google.common.base.Stopwatch
import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.tacmapunits.util.mustBeNull
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import org.xguzm.pathfinding.grid.NavigationGrid
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import java.lang.Exception
import java.lang.IllegalArgumentException

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
        if (acquireBestPathTo(logicalCharacter, endLocation = closestEnemyCharacter.tileLocation, allowEndingOnLastTile = true) == null){
            return null
        }

        return closestEnemyCharacter
    }

    val MAX_RADIUS = 5
    public fun findClosestWalkableTileTo(origin: TileLocation,
                                           logicalCharacter: LogicalCharacter,
                                           allowEndingOnOrigin: Boolean = false) : TileLocation?{
        // look at a max radius of five, then give up
        val tileSet = HashSet<TileLocation>()
        tileSet.add(origin)
        for (i in 0 .. 5){
            val nextRing = tileSet.flatMap { getNeighbors(it) }
            tileSet.addAll(nextRing)
            // todo: clumsy implementation
            for (tile in nextRing){
                if (tile == origin && !allowEndingOnOrigin){
                    continue
                }
                if (tacticalMapAlgorithms.canWalkOnTile(tileLocation = tile,
                                logicalCharacter =logicalCharacter )){
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


    override public fun acquireBestPathTo(character: LogicalCharacter,
                                          endLocation: TileLocation,
                                          allowEndingOnLastTile: Boolean,
                                          allowFuzzyMatching: Boolean,
                                          restrictToCharacterMoveRange: Boolean) : Collection<PathfindingTileLocation>?{

        var trueEndLocation = endLocation
        if (!tacticalMapAlgorithms.canWalkOnTile(character, endLocation)){
            if (!allowFuzzyMatching){
                throw IllegalArgumentException("Character ${character.tacMapUnit.templateName} cannot walk on $endLocation")
            }
            Logging.DebugPathfinding("Character ${character.tacMapUnit.templateName} cannot walk on $endLocation; attempting substitute location")
            trueEndLocation = findClosestWalkableTileTo(endLocation, character, false)?:return null
        }
        val stopwatch = Stopwatch.createStarted()
        try {
            var opt = GridFinderOptions()
            opt.allowDiagonal = false
            // navigationGrid.setNodes(convertToNodes())
            var finder = AStarGridFinder(PathfindingTileLocation::class.java, opt)

            trueEndLocation.getCharacter().mustBeNull()
            val bestPath = finder.findPath(character.tileLocation.x,
                    character.tileLocation.y,
                    trueEndLocation.x,
                    trueEndLocation.y,
                    this.navigationGrid)?.toList()


            // println("${if (bestPath == null) "FAILURE" else "SUCCESS"} in finding route between ${startCharacter.tileLocation} and ${endLocation}")

            if (bestPath == null){
                return null
            }
            var protoAnswer:List<PathfindingTileLocation> = listOf()

            if (!allowEndingOnLastTile && trueEndLocation == endLocation) {
                if (bestPath.size <= 1) {
                    return listOf()
                }
                val answer = bestPath.subList(0, bestPath.size - 1)
                protoAnswer = answer
            } else {
                protoAnswer = bestPath
            }
            if (restrictToCharacterMoveRange){
                protoAnswer = truncateToAllowedCharacterMovementForThisTurn(character, protoAnswer)
            }
            return protoAnswer
        } catch(e: Exception){
            println("ERROR when attempting to go from ${character.tileLocation} to ${endLocation} with allowEndingOnLastTile=${allowEndingOnLastTile}")
            throw e;
        } finally{
            stopwatch.stop()
            // println("Millis elapsed for pathfinding:" + stopwatch.elapsed(TimeUnit.MILLISECONDS))
        }
    }

    private fun truncateToAllowedCharacterMovementForThisTurn(character: LogicalCharacter, protoAnswer: List<PathfindingTileLocation>): List<PathfindingTileLocation> {
        if (protoAnswer.size <= character.tacMapUnit.movesPerTurn){
            return protoAnswer
        }
        return protoAnswer.subList(0, character.tacMapUnit.movesPerTurn)
    }
}