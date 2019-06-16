package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntityFactory
import javax.inject.Inject

public class TiledMapModifier  @Inject constructor (val logicalTileTracker: LogicalTileTracker,
                                                    val tileEntityFactory: TileEntityFactory,
                                                    val tileMapProvider: TileMapProvider,
                                                    val tiledMapStageProvider: TacticalTiledMapStageProvider){
    fun purgeTile(tileLocation: TileLocation,
                    layer: TileLayer){
        val tile = logicalTileTracker.getLogicalTileFromLocation(tileLocation)
        val tilesAtThisLayer = tile!!.allTilesAtThisSquare.first{it.tileLayer == layer}
        tilesAtThisLayer.tiledCell.tile = null // Removing this tile
    }

    // use insertDoor instead.
    fun placeDoor(tileLocation: TileLocation){
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        val doorEntityProtoActor = DoorEntity.closedDoorProtoActor
        val actor = doorEntityProtoActor.toActor().actor;
        tiledMapStageProvider.tiledMapStage.addActor(actor)
        actor.setBoundingBox(boundingBox)
        val doorEntity = tileEntityFactory.createDoor(tileLocation, actor)
        // remove wall if it exists
        logicalTileTracker.removeWallAtTile(tileLocation)
        logicalTileTracker.tileEntities.add(doorEntity)
    }
}