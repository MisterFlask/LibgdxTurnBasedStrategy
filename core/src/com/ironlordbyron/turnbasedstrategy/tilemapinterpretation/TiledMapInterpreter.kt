package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

/// Responsible for figuring out which of the Tiled tiles need to become actors
/// so that they can be manipulated.
public class TiledMapInterpreter @Inject constructor(val tileEntityFactory: TileEntityFactory,
                                                     val tiledMapStageProvider: TacticalTiledMapStageProvider,
                                                     val logicalTileTracker: LogicalTileTracker,
                                                     val tileMapProvider: TileMapProvider){



    fun getPossiblePlayerSpawnPositions(map: TiledMap): Collection<TileLocation> {
        return map.getTilesInObjectByType("PLAYER_SPAWN").flatMap{it -> it}
    }

    fun getPossibleEnemySpawnPositions(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("ENEMY_SPAWN").flatMap{it -> it}
    }
    fun getSpawners(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("SPAWNER").flatMap{it -> it}
    }
    fun getMasterOrgan(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("MASTER_ORGAN").flatMap { it }
    }
    fun getShieldingOrgan(map: TiledMap)  : Collection<TileLocation> {
        return map.getTilesInObjectByType("SHIELDING_ORGAN").flatMap { it }
    }

    fun initializeTileEntities(tileMap: TiledMap, tileLocation: TileLocation){
        val layersAtLocation = getAllTilesAtXY(tileMap, tileLocation)
        val cells = layersAtLocation.map{it?.tiledCell}
        val entities = ArrayList<TileEntity>()
        for (cell in cells){
            val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
            val doorStr = "door"
            if (hasProp(cell, doorStr)){
                cell.tile.id
                val actor = ActorFromTiledTextureRegion(cell).imageActor;
                tiledMapStageProvider.tiledMapStage.addActor(actor)
                actor.setBoundingBox(boundingBox)
                entities.add(tileEntityFactory.createDoor(tileLocation, actor))
            }
            val wallStr = "wall"
            if (hasProp(cell, wallStr)){
                val actor = ActorFromTiledTextureRegion(cell).imageActor;
                tiledMapStageProvider.tiledMapStage.addActor(actor)
                entities.add(tileEntityFactory.createWall(tileLocation, actor))
                actor.setBoundingBox(boundingBox)
            }
        }
        logicalTileTracker.tileEntities.addAll(entities)
    }

    private fun hasProp(cell: TiledMapTileLayer.Cell, doorStr: String) =
            cell?.tile?.properties?.get(doorStr) != null

    fun retrieveTerrainType(tileMap: TiledMap, tileLocation: TileLocation) : TerrainType{
        val layersAtLocation = getAllTilesAtXY(tileMap, tileLocation)
        val atLayer = layersAtLocation.getCellByLayer(TileLayer.FEATURE)
        if (atLayer != null){
            val isMountain = atLayer.tiledCell.tile.properties["mountain"] as Boolean
            if (isMountain){
                return TerrainType.MOUNTAIN
            }
            val isTrees = atLayer.tiledCell.tile.properties["trees"] as Boolean

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