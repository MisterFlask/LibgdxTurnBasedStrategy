package com.ironlordbyron.turnbasedstrategy.ai

import com.google.inject.ImplementedBy
import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import java.lang.IllegalArgumentException
import java.util.*

@ImplementedBy(BespokePathfinder::class)
interface Pathfinder {
    fun acquireBestPathTo(character: LogicalCharacter, endLocation: TileLocation, allowEndingOnLastTile: Boolean,
                          allowFuzzyMatching:Boolean = true,restrictToCharacterMoveRange: Boolean = false) : Collection<TileLocation>?
}

val tacticalMapAlgorithms by LazyInject(TacticalMapAlgorithms::class.java)
fun TileLocation.isPassableBy(character: LogicalCharacter): Boolean {
    return tacticalMapAlgorithms.canWalkOnTile(character, this)
}

data class TileLocationWithPredecessor(val tileLocation: TileLocation, val predecessor: TileLocationWithPredecessor?)

class BespokePathfinder : Pathfinder{

    override fun acquireBestPathTo(character: LogicalCharacter,
                                   endLocation: TileLocation,
                                   allowEndingOnLastTile: Boolean,
                                   allowFuzzyMatching: Boolean,
                                   restrictToCharacterMoveRange: Boolean): Collection<TileLocation>? {
        if (endLocation == character.tileLocation){
            return listOf()
        }
        var trueEndLocation = endLocation

        if (!tacticalMapAlgorithms.canWalkOnTile(character, endLocation)){
            if (!allowFuzzyMatching){
                throw IllegalArgumentException("Character ${character.tacMapUnit.templateName} cannot walk on $endLocation")
            }
            Logging.DebugPathfinding("Character ${character.tacMapUnit.templateName} cannot walk on $endLocation; attempting substitute location")
            trueEndLocation = findClosestWalkableTileTo(endLocation, character, false)?:return null
        }

        var rawPath = getRawPathTo(character, trueEndLocation)
        if (rawPath == null){
            return null
        }
        var path = rawPath
        if (restrictToCharacterMoveRange){
            val moveRange = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(character)
            val lastTileInPath = path.last{moveRange.contains(it)}
            val indexOfLastTile = path.lastIndexOf(lastTileInPath)
            path = path.take(indexOfLastTile)
        }
        if (!allowEndingOnLastTile){
            path = path.dropLast(1)
        }
        return path
    }

    val MAX_RADIUS = 5
    public fun findClosestWalkableTileTo(origin: TileLocation,
                                         logicalCharacter: LogicalCharacter,
                                         allowEndingOnOrigin: Boolean = false) : TileLocation?{
        // look at a max radius of five, then give up
        val tileSet = HashSet<TileLocation>()
        tileSet.add(origin)
        for (i in 0 .. 5){
            val nextRing = tileSet.flatMap { it.neighbors() }
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
    private fun unroll(root: TileLocationWithPredecessor?): List<TileLocation> {
        val list= ArrayList<TileLocation>()
        var current = root
        while(current != null){
            list.add(current.tileLocation)
            current = current.predecessor
        }
        return list.reversed()
    }

    private fun getRawPathTo(character:LogicalCharacter, endLocation: TileLocation): List<TileLocation>? {
        val locationsProcessed = HashSet<TileLocation>()
        val locationsToProcess = PriorityQueue<TileLocationWithPredecessor>(DistanceToDestinationComparator(TileLocationWithPredecessor(endLocation, null)))
        locationsToProcess.addAll(character.tileLocation.neighbors().map{ TileLocationWithPredecessor(it, null) })
        while (locationsToProcess.isNotEmpty()){
            val nextLocationToCheck = locationsToProcess.poll()
            if (!nextLocationToCheck.tileLocation.isPassableBy(character)){
                continue
            }
            if (locationsProcessed.contains(nextLocationToCheck.tileLocation)){
                continue
            }
            if (nextLocationToCheck.tileLocation == endLocation){
                return unroll(nextLocationToCheck)
            }
            locationsProcessed.add(nextLocationToCheck.tileLocation)

            val neighbors = nextLocationToCheck.tileLocation.neighbors().map{ TileLocationWithPredecessor(it, nextLocationToCheck) }
            locationsToProcess.addAll(neighbors)
        }
        return null
    }


}

class DistanceToDestinationComparator(val destination: TileLocationWithPredecessor) : Comparator<TileLocationWithPredecessor>{
    override fun compare(o1: TileLocationWithPredecessor, o2: TileLocationWithPredecessor): Int {
        return o1.tileLocation.distanceTo(destination.tileLocation).compareTo(o2.tileLocation.distanceTo(destination.tileLocation))
    }
}