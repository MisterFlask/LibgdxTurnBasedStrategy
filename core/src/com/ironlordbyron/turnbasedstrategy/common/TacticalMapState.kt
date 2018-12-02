package com.ironlordbyron.turnbasedstrategy.common

import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTileTracker
import javax.inject.Singleton

/**
 * Responsible for understanding the basic model-side board algorithms and
 * tracking model-side board data.
 */
@Singleton
class TacticalMapState @Inject constructor(val logicalTileTracker: LogicalTileTracker){

    val locations = HashMap<TileLocation, TacticalMapTileState>()
    val listOfCharacters = ArrayList<LogicalCharacter>()

    val listOfEnemyCharacters: List<LogicalCharacter>
    get() = listOfCharacters.filter{!it.playerControlled}

    fun getCharacterAtLocation(tileLocation: TileLocation): LogicalCharacter? {
        return listOfCharacters.firstOrNull{it.tileLocation == tileLocation}
    }

    fun isTileUnoccupied(tileLocation: TileLocation) : Boolean{
        return getCharacterAtLocation(tileLocation) == null
    }

    fun init(){
        for (tile in logicalTileTracker.tiles){
            locations.put(tile.location, TacticalMapTileState(tile.location, true))
        }
    }

    fun getWhereCharacterCanMoveTo(character: LogicalCharacter): Collection<TileLocation> {
        val tiles = getWalkableTileLocationsUpToNAway(character.tacMapUnit.movesPerTurn, character.tileLocation, character)
        return tiles
    }

    /**
     * e.g. , getting tiles up to 1 away will always return 4 tiles assuming all are passable.
     * Will not include the origin tile.
     */
    fun getWalkableTileLocationsUpToNAway(n: Int, origin: TileLocation, character: LogicalCharacter): Collection<TileLocation> {
        var iteration = n
        val tilesAlreadyProcessed = HashSet<TileLocation>()
        var nextSetOfTiles = HashSet<TileLocation>()
        var tilesToBeProcessed = hashSetOf(origin)
        while(tilesToBeProcessed.isNotEmpty() && iteration > 0){
            val nextTile = tilesToBeProcessed.first()
            tilesToBeProcessed.remove(nextTile)
            val neighbors = getPassableNeighbors(nextTile, character)
            nextSetOfTiles.addAll(neighbors.filter{tilesAlreadyProcessed.doesNotContain(it)})
            tilesAlreadyProcessed.add(nextTile)
            if (tilesToBeProcessed.isEmpty()){
                tilesToBeProcessed.addAll(nextSetOfTiles)
                iteration--
            }
        }
        return tilesAlreadyProcessed
    }

    private fun getPassableNeighbors(nextTile: TileLocation, character: LogicalCharacter): Collection<TileLocation> {
        val north = nextTile.copy(y=nextTile.y+1)
        val south = nextTile.copy(y=nextTile.y-1)
        val east = nextTile.copy(x=nextTile.x+1)
        val west = nextTile.copy(x=nextTile.x-1)

        return listOf(north,south,east,west).filter{isPassable(it, character)}
    }

    private fun isPassable(loc: TileLocation, character: LogicalCharacter): Boolean {
        val tile = logicalTileTracker.tiles.firstOrNull{it.location == loc}
        if (tile == null){
            return false
        }
        return canWalkOnTile(character, loc)
    }
    fun canWalkOnTile(logicalCharacter: LogicalCharacter, tileLocation: TileLocation): Boolean{
        val terrain = logicalTileTracker.getLogicalTileFromLocation(tileLocation)!!.terrainType
        return logicalCharacter.tacMapUnit.walkableTerrainTypes.contains(terrain) && isTileUnoccupied(tileLocation)
    }

}

private fun <E> java.util.HashSet<E>.doesNotContain(it: E): Boolean {
    return !this.contains(it)
}

data class TacticalMapTileState(val tileLocation: TileLocation,
                                val passable: Boolean)

