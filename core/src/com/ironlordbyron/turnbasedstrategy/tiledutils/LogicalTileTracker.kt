package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WallEntity
import javax.inject.Singleton


@Singleton
class LogicalTileTracker {

    val tileEntities = ArrayList<TileEntity>()

    val tiles = ArrayList<LogicalTile>()

    fun addTile(logicalTile: LogicalTile) {
        tiles.add(logicalTile)
    }

    fun isDoor(location: TileLocation): Boolean{
        // Now, we check the other layers for walls/doors that might overwrite the tile
        return getEntitiesAtTile(location).any{it is DoorEntity}
    }
    fun isWall(location: TileLocation): Boolean{
        // Now, we check the other layers for walls/doors that might overwrite the tile
        return getEntitiesAtTile(location).any{it is WallEntity}
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

    fun removeEntity(tileEntity: WallEntity) {
        throw NotImplementedError()
    }
}