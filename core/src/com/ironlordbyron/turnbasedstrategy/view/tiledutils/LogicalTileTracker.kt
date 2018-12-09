package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import javax.inject.Singleton


@Singleton
class LogicalTileTracker {
    val tiles = ArrayList<LogicalTile>()

    fun addTile(logicalTile: LogicalTile) {
        tiles.add(logicalTile)
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