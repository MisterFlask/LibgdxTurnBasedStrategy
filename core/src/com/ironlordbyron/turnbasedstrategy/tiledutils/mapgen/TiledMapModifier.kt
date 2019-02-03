package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.TileLayer
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntityFactory
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject

public class TiledMapModifier  @Inject constructor (val logicalTileTracker: LogicalTileTracker,
                                                    val tileEntityFactory: TileEntityFactory){
    fun purgeTile(tileLocation: TileLocation,
                    layer: TileLayer){
        val tile = logicalTileTracker.getLogicalTileFromLocation(tileLocation)
        tile!!.cell.tile = null // Removing this tile
    }

    fun placeDoor(tileLocation: TileLocation){
        val doorEntityProtoActor = DoorEntity.closedDoorProtoActor
        val doorEntity = tileEntityFactory.createDoor(tileLocation, doorEntityProtoActor.toActor().actor)
    }



}