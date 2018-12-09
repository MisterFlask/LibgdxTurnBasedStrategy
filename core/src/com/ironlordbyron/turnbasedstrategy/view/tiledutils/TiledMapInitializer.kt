package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMap
import javax.inject.Inject


public class TiledMapInitializer @Inject constructor(val logicalTileTracker: LogicalTileTracker){
    fun initializeFromTilemap(tiledMap: TiledMap){
        for (tile in logicalTileTracker.tiles){
            if (tile.terrainTypeFromUnderlyingTile == TerrainType.MOUNTAIN){
                tile.terrainType = TerrainType.MOUNTAIN
            }
            // Now, we check the object layer for physical features we need to throw down!
        }
        for (rect in tiledMap.getObjectLayerRectangles()){
            
        }
    }
}