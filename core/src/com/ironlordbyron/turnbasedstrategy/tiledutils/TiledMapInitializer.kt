package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMap
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WallEntity
import javax.inject.Inject


public class TiledMapInitializer @Inject constructor(val logicalTileTracker: LogicalTileTracker,
                                                     val tiledMapInterpreter: TiledMapInterpreter){
    fun initializeFromTilemap(tiledMap: TiledMap){
        for (tile in logicalTileTracker.tiles){
            // Note: we only do the base terrain types here.
            if (tile.terrainTypeFromUnderlyingTile == TerrainType.MOUNTAIN){
                tile.terrainType = TerrainType.MOUNTAIN
            }
            tile.terrainType = tiledMapInterpreter.retrieveTerrainType(tiledMap, tile.location)
        }
    }
}