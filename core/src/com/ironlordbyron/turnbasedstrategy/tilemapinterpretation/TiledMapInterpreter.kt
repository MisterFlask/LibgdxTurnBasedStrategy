package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.*

public class TiledMapInterpreter(){



    fun getPossiblePlayerSpawnPositions(map: TiledMap): Collection<TileLocation> {
        return map.getTilesInObjectByType("PLAYER_SPAWN").flatMap{it -> it}
    }
    fun getPossibleEnemySpawnPositions(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("ENEMY_SPAWN").flatMap{it -> it}
    }
    fun retrieveTerrainType(tileMap: TiledMap, tileLocation: TileLocation) : TerrainType{
        val layersAtLocation = getAllTilesAtXY(tileMap, tileLocation)
        val atLayer = layersAtLocation.getCellByLayer(TileLayer.FEATURE)
        if (atLayer != null){
            val isMountain = atLayer.tiledCell.tile.properties["mountain"] as Boolean
            if (isMountain){
                return TerrainType.MOUNTAIN
            }
            val isTrees = atLayer.tiledCell.tile.properties["trees"]
        }
        return TerrainType.GRASS
    }

    fun getAllTilesAtXY(tileMap: TiledMap, tileLocation: TileLocation): List<TiledMapStage.TiledCellAgglomerate> {
        val layers = tileMap.layers
                .filter { it is TiledMapTileLayer }
                .map { it as TiledMapTileLayer }
                .filter { it.getCell(tileLocation.x, tileLocation.y) != null }
                .filter { TileLayer.getTileLayerFromName(it.name) != null }
                .map { TiledMapStage.TiledCellAgglomerate(it.getCell(tileLocation.x, tileLocation.y), TileLayer.getTileLayerFromName(it.name)!!) }
        return layers
    }
}

public fun List<TiledMapStage.TiledCellAgglomerate>.getCellByLayer(tileLayer: TileLayer): TiledMapStage.TiledCellAgglomerate? {
    return this.filter{it.tileLayer == tileLayer}.firstOrNull()
}

public class KnownObjectFactory(){
    fun build(knownObjectType: KnownObjectType){

    }

}

public enum class KnownObjectType{
    ENEMY_SPAWN,
    ENEMY_SPAWNER,
    PLAYER_SPAWN
}
public enum class KnownProperties(val stringName: String){
    TYPE("type"),
    MOUNTAIN("mountain")
}

public class TileFact(val tileLocation: TileLocation, val key: String, val value: String)