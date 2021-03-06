package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.logicalTile
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WallEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogicalTileTracker @Inject constructor (val eventNotifier: EventNotifier) : GameEventListener {

    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is TacticalGameEvent.INITIALIZE -> {
                tileEntities.clear()
                tiles.clear()
            }
        }
    }
    init {
        eventNotifier.registerGameListener(this)
    }

    val tileEntities = ArrayList<TileEntity>()

    val tiles = HashMap<TileLocation, LogicalTile>()

    fun <T: TileEntity> getEntitiesOfType(clazz: Class<T>): List<TileEntity> {
        return tileEntities.filter{clazz.isInstance(it)}
    }

    fun addTile(logicalTile: LogicalTile) {
        tiles.put(logicalTile.location, logicalTile)
    }

    public fun getNeighborTiles(tile: TileLocation): List<LogicalTile> {
        val north = tile.copy(y=tile.y+1)
        val south = tile.copy(y=tile.y-1)
        val east = tile.copy(x=tile.x+1)
        val west = tile.copy(x=tile.x-1)

        return listOf(north,south,east,west)
                .map{getLogicalTileFromLocation(it)}
                .filter{it != null} as List<LogicalTile>
    }

    public fun getNeighbors(tile: TileLocation): List<TileLocation> {
        val north = tile.copy(y=tile.y+1)
        val south = tile.copy(y=tile.y-1)
        val east = tile.copy(x=tile.x+1)
        val west = tile.copy(x=tile.x-1)

        return listOf(north,south,east,west)
                .filter{getLogicalTileFromLocation(it) != null}
    }

    fun isDoor(location: TileLocation): Boolean{
        // Now, we check the other layers for walls/doors that might overwrite the tile
        return getEntitiesAtTile(location).any{it is DoorEntity}
    }

    fun <T : TileEntity> hasEntityAtLocation(location: TileLocation, clazz: Class<T>): Boolean {
        return getEntitiesAtTile(location).any{clazz.isInstance(it)}
    }

    fun isWall(location: TileLocation): Boolean{
        // Now, we check the other layers for walls/doors that might overwrite the tile
        return getEntitiesAtTile(location).any{it is WallEntity}
    }

    fun getEntitiesAtTile(location: TileLocation): List<TileEntity> {
        return tileEntities.filter{it -> it.tileLocations.contains(location)}
    }

    fun removeWallAtTile(location: TileLocation){
        val wall=  tileEntities.first{it is WallEntity && it.tileLocation == location}
        wall.actor.remove()
        tileEntities.removeIf{it is WallEntity && it.tileLocation == location}
    }

    fun getLogicalTileFromTile(tile: TiledMapTile): LogicalTile? {
        return tiles.values.firstOrNull { it.terrainTile === tile }
    }

    fun getLogicalTileFromLocation(loc: TileLocation): LogicalTile? {
        return tiles[loc]
    }

    fun getLibgdxCoordinatesFromLocation(loc: TileLocation): LibgdxLocation {
        val tileActor = tiles[loc]!!.clickListeningActor

        return LibgdxLocation(tileActor.x.toInt(), tileActor.y.toInt()) // TODO: Verify
    }

    fun height() : Int{
        return tiles.values.maxBy{it.location.y}!!.location.y + 1
    }
    fun width() : Int{
        return tiles.values.maxBy{it.location.x}!!.location.x + 1
    }

    fun removeEntity(tileEntity: WallEntity) {
        throw NotImplementedError()
    }
}

val logicalTileTracker: LogicalTileTracker by lazy{
    GameModuleInjector.generateInstance(LogicalTileTracker::class.java)
}
fun TileLocation.toLibgdxCoordinates() : LibgdxLocation{
    return logicalTileTracker.getLibgdxCoordinatesFromLocation(this)
}

fun TileLocation.fogStatus() : FogStatus{
    return logicalTileTracker.tiles[this]!!.underFogOfWar
}