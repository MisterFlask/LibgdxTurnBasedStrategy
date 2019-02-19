package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.TileLayer
import com.ironlordbyron.turnbasedstrategy.tiledutils.setBoundingBox
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.ActorFromTiledTextureRegion
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntityFactory
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject

public class TiledMapModifier  @Inject constructor (val logicalTileTracker: LogicalTileTracker,
                                                    val tileEntityFactory: TileEntityFactory,
                                                    val tileMapProvider: TileMapProvider,
                                                    val tiledMapStageProvider: TacticalTiledMapStageProvider){
    fun purgeTile(tileLocation: TileLocation,
                    layer: TileLayer){
        val tile = logicalTileTracker.getLogicalTileFromLocation(tileLocation)
        tile!!.allTilesAtThisSquare.first{it.tileLayer == layer}.tiledCell.tile = null // Removing this tile
    }

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