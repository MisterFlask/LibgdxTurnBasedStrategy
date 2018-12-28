package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import javax.inject.Singleton


@Singleton
class LogicalTileTracker {

    val tileEntities = ArrayList<TileEntity>()

    val tiles = ArrayList<LogicalTile>()

    fun addTile(logicalTile: LogicalTile) {
        tiles.add(logicalTile)
    }

    fun getEntitiesAtTile(location: TileLocation): List<TileEntity> {
        return tileEntities.filter{it -> it.tileLocation == location}
    }

    fun getLogicalTileFromTile(tile: TiledMapTile): LogicalTile? {
        return tiles.firstOrNull { it.terrainTile === tile }
    }

    fun getLogicalTileFromLocation(loc: TileLocation): LogicalTile? {
        return tiles.firstOrNull { it.location == loc }
    }

    fun getLibgdxCoordinatesFromLocation(loc: TileLocation): LibgdxLocation {
        val tileActor = tiles.first { it.location == loc }.actor

        return LibgdxLocation(tileActor.x.toInt(), tileActor.y.toInt()) // TODO: Verify
    }

    fun height() : Int{
        return tiles.maxBy{it.location.y}!!.location.y + 1
    }
    fun width() : Int{
        return tiles.maxBy{it.location.x}!!.location.x + 1
    }
}