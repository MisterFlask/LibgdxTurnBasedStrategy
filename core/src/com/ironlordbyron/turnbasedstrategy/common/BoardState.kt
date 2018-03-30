package com.ironlordbyron.turnbasedstrategy.common

import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Singleton

/**
 * Responsible for understanding the basic model-side board algorithms and
 * tracking model-side board data.
 */
@Singleton
class BoardState @Inject constructor(val logicalTileTracker: LogicalTileTracker){

    val locations = HashMap<TileLocation, TacticalMapTileState>()
    val listOfCharacters = ArrayList<LogicalCharacter>()

    fun getCharacterAtLocation(tileLocation: TileLocation): LogicalCharacter? {
        return listOfCharacters.firstOrNull{it.tileLocation == tileLocation}
    }

    fun init(){
        for (tile in logicalTileTracker.tiles){
            locations.put(tile.location, TacticalMapTileState(tile.location, true))
        }
    }

    /**
     * e.g. , getting tiles up to 1 away will always return 4 tiles assuming all are passable.
     * Will not include the origin tile.
     */
    fun getTileLocationsUpToNAway(n: Int, origin: TileLocation): Collection<TileLocation> {
        var iteration = n
        val tilesAlreadyProcessed = HashSet<TileLocation>()
        var nextSetOfTiles = HashSet<TileLocation>()
        var tilesToBeProcessed = hashSetOf(origin)
        while(tilesToBeProcessed.isNotEmpty() && iteration > 0){
            val nextTile = tilesToBeProcessed.first()
            tilesToBeProcessed.remove(nextTile)
            val neighbors = getNeighbors(nextTile)
            nextSetOfTiles.addAll(neighbors.filter{tilesAlreadyProcessed.doesNotContain(it)})
            tilesAlreadyProcessed.add(nextTile)
            if (tilesToBeProcessed.isEmpty()){
                tilesToBeProcessed.addAll(nextSetOfTiles)
                iteration--
            }
        }
        return tilesAlreadyProcessed
    }

    private fun getNeighbors(nextTile: TileLocation): Collection<TileLocation> {
        val north = nextTile.copy(y=nextTile.y+1)
        val south = nextTile.copy(y=nextTile.y-1)
        val east = nextTile.copy(x=nextTile.x+1)
        val west = nextTile.copy(x=nextTile.x-1)
        return listOf(north,south,east,west)
    }
}

private fun <E> java.util.HashSet<E>.doesNotContain(it: E): Boolean {
    return !this.contains(it)
}

data class TacticalMapTileState(val tileLocation: TileLocation,
                                val passable: Boolean)

