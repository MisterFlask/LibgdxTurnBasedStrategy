package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.maps.tiled.TiledMap
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTile
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType
import com.ironlordbyron.turnbasedstrategy.tiledutils.getTilesByKeyValuePairs
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledCellData
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

data class TileLocation(val x: Int, val y: Int){

    public override fun toString(): String{
        return "[$x,$y]"
    }
}


private val gameState: TacticalMapState by lazy {
    GameModuleInjector.generateInstance(TacticalMapState::class.java)
}
public fun TileLocation.getCharacter() : LogicalCharacter? {
    return gameState.characterAt(this)
}

public fun TileLocation.distanceTo(tileLocation: TileLocation): Int {
    return Math.abs(this.x - tileLocation.x) + Math.abs(this.y - tileLocation.y)
}

public operator fun TileLocation.minus(tileLocation: TileLocation): TileLocation{
    return TileLocation(x = this.x - tileLocation.x, y = this.y - tileLocation.y)
}

public operator fun TileLocation.plus(tileLocation: TileLocation) : TileLocation{
    return TileLocation(this.x + tileLocation.x, this.y + tileLocation.y)
}

val tiledMapInterpreter: TiledMapInterpreter by lazy{
    GameModuleInjector.generateInstance(TiledMapInterpreter::class.java)
}
val tiledMapProvider: TileMapProvider by lazy{
    GameModuleInjector.generateInstance(TileMapProvider::class.java)
}
public fun TileLocation.terrainType(): TerrainType? {
    return logicalTileTracker.getLogicalTileFromLocation(this)?.terrainType
}

val logicalTileTracker: LogicalTileTracker by lazy{
    GameModuleInjector.generateInstance(LogicalTileTracker::class.java)
}

public fun TileLocation.logicalTile() : LogicalTile? {
    return logicalTileTracker.getLogicalTileFromLocation(this)
}

public fun TileLocation.entity(): TileEntity?{
    return logicalTileTracker.getEntitiesAtTile(this).firstOrNull()
}

public fun TileLocation.neighbors() : List<TileLocation>{
    return logicalTileTracker.getNeighbors(this)
}

public fun TileLocation.terrainProperties() : Collection<TiledCellData>{
    return tiledMapInterpreter.getTerrainPropertiesAtTileLocation(tiledMapProvider.tiledMap, this)
}

val tacticalMapAlgorithms: TacticalMapAlgorithms by LazyInject(TacticalMapAlgorithms::class.java)

public fun TileLocation.floodFill(pred: (TileLocation)->Boolean): ArrayList<TileLocation> {
    return tacticalMapAlgorithms.floodFindTilesMatchingPredicate(this, pred)
}

public fun TileLocation.nearestUnoccupiedSquares(squaresNeeded: Int): Collection<TileLocation>{
    val squaresFound = ArrayList<TileLocation>()
    val squaresToProcess = ArrayDeque<TileLocation>()
    squaresToProcess.add(this)
    while(squaresFound.size < squaresNeeded){
        val current = squaresToProcess.remove()
        val neighbors = current.neighbors()
        val eligible =
                neighbors
                .filter{it.getCharacter() == null}
                .filter{!squaresFound.contains(it)}
        squaresFound.addAll(eligible)
        squaresToProcess.addAll(neighbors)
    }
    return squaresFound.subList(0, squaresNeeded)

}

public fun Collection<TileLocation>.thatIsClosestTo(tileLocation: TileLocation): TileLocation {

    return this.minBy{tileLocation.distanceTo(it)}!!
}

public fun Collection<TileLocation>.expandByRadius(radius: Int): HashSet<TileLocation> {
    val tilesToReturn = this.toHashSet()
    for (i in 0 .. radius){
        for (tile in tilesToReturn.toHashSet()){
            tilesToReturn.addAll(tile.neighbors())
        }
    }
    return tilesToReturn

}