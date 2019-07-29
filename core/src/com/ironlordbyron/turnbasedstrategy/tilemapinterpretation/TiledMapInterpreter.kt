package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tileentity.CityTileEntity
import javax.inject.Inject

/// Responsible for figuring out which of the Tiled tiles need to become actors
/// so that they can be manipulated.
public class TiledMapInterpreter @Inject constructor(val tileEntityFactory: TileEntityFactory,
                                                     val tiledMapStageProvider: TacticalTiledMapStageProvider,
                                                     val logicalTileTracker: LogicalTileTracker,
                                                     val tileMapProvider: TileMapProvider){


    fun getPossiblePlayerSpawnPositions(map: TiledMap): Collection<TileLocation> {
        return map.getTilesInObjectByType("PLAYER_SPAWN", false)
    }

    fun getPossibleEnemySpawnPositions(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("ENEMY_SPAWN", false)
    }
    fun getSpawners(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("SPAWNER", false)
    }
    fun getMasterOrgan(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("MASTER_ORGAN", false)
    }
    fun getShieldingOrgan(map: TiledMap)  : Collection<TileLocation> {
        return map.getTilesInObjectByType("SHIELDING_ORGAN", false)
    }

    fun getSpawnedTacMapUnit(map: TiledMap, id: String): List<TileLocation> {
        return map.getTilesByKeyValuePairs(listOf(
                TileKeyValuePair("type", "UNIT_SPAWN_POINT"),
                TileKeyValuePair("unit_id", id)
        )
        )
    }

    fun getTerrainPropertiesAtTileLocation(tileMap: TiledMap, tileLocation: TileLocation): List<TiledCellData> {
        val layersAtLocation = getAllTilesAtXY(tileMap, tileLocation)
        val cells = layersAtLocation.map{it?.tiledCell}
        val cellData = arrayListOf<TiledCellData>()
        for (cell in cells){
            val iterator = cell.tile?.properties?.keys
            val properties = iterator?.asSequence()?.asIterable()?:listOf()
            cellData.add(TiledCellData(properties = properties.toList(), tileLocation = tileLocation,
                    cell = cell))
        }
        return cellData
    }

    val tileEntityRegistrar: TileEntityRegistrar by LazyInject(TileEntityRegistrar::class.java)
    val tacticalTiledMapStageProvider: TacticalTiledMapStageProvider by LazyInject(TacticalTiledMapStageProvider::class.java)
    fun initializeTileEntities(tileMap: TiledMap, tileLocation: TileLocation){
        val entities = ArrayList<TileEntity>()
        val entity = tileEntityRegistrar.registerEntity(tileLocation)
        if (entity != null) {
            tacticalTiledMapStageProvider.tiledMapStage.addActor(entity.actor)
            entities.add(entity)
        }

        logicalTileTracker.tileEntities.addAll(entities)
    }

    private fun isWater(tileMap: TiledMap, tileLocation: TileLocation): Boolean {
        val layersAtLocation = getAllTilesAtXY(tileMap, tileLocation)
        if (layersAtLocation.getCellByLayer(TileLayer.BASE)?.tiledCell?.tile?.properties?.containsKey("ground")?:false){
            return false
        }
        return true
    }

    private fun hasProp(cell: TiledMapTileLayer.Cell, doorStr: String) =
            cell.tile?.properties?.get(doorStr) != null

    fun retrieveTerrainType(tileMap: TiledMap, tileLocation: TileLocation) : TerrainType{
        val layersAtLocation = getAllTilesAtXY(tileMap, tileLocation)
        val atBaseLayer = layersAtLocation.getCellByLayer(TileLayer.BASE)
        if (atBaseLayer != null && atBaseLayer.tiledCell.tile != null){
            val isMountain = atBaseLayer.tiledCell.tile.properties["mountain"]
            if (isMountain != null){
                return TerrainType.MOUNTAIN
            }
        }

        val atFeatureLayer = layersAtLocation.getCellByLayer(TileLayer.FEATURE)
        if (isWater(tileMap, tileLocation)){
            return TerrainType.WATER
        }
        if (atFeatureLayer != null
                && atFeatureLayer.tiledCell.tile != null){
            val isMountain = atFeatureLayer.tiledCell.tile.properties["mountain"]
            if (isMountain != null){
                return TerrainType.MOUNTAIN
            }
            val isTrees = atFeatureLayer.tiledCell.tile.properties["forest"]
            if (isTrees != null){
                return TerrainType.FOREST
            }
        }
        return TerrainType.GRASS
    }

    fun validateTileMap(tileMap: TiledMap){

        val layers = tileMap.layers
                .filter { it is TiledMapTileLayer }
                .map { it as TiledMapTileLayer }
        val featuresLayer = layers.filter{it.name == TileLayer.BASE.layerName}
        val baseLayer = layers.filter{it.name == TileLayer.BASE.layerName}
        if (featuresLayer.isEmpty()){
            throw IllegalStateException("Tile layer lacks Features Layer")
        }
        if (baseLayer.isEmpty()){
            throw java.lang.IllegalStateException("Tile layer lacks Base Layer")
        }
    }

    fun getAllTilesAtXY(tileMap: TiledMap, tileLocation: TileLocation): List<TiledMapStage.TiledCellAgglomerate> {
        val layers = tileMap.layers
                .filter { it is TiledMapTileLayer }
                .map { it as TiledMapTileLayer }
        val withCellHere = layers.filter { it.getCell(tileLocation.x, tileLocation.y) != null }
        val toAggloms = withCellHere
                .map { TiledMapStage.TiledCellAgglomerate(it.getCell(tileLocation.x, tileLocation.y), TileLayer.getTileLayerFromName(it.name)) }
        return toAggloms
    }
}
val tiledMapStageProvider: TacticalTiledMapStageProvider by LazyInject(TacticalTiledMapStageProvider::class.java)
val tileMapProvider: TileMapProvider by LazyInject(TileMapProvider::class.java)
data class TiledCellData(val properties: Collection<String>,
                         val cell: TiledMapTileLayer.Cell,
                         val tileLocation: TileLocation){
    fun getActor(): Image {
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        val actor = ActorFromTiledTextureRegion(cell).imageActor;
        actor.setBoundingBox(boundingBox)
        return actor
    }
}

public fun List<TiledMapStage.TiledCellAgglomerate>.getCellByLayer(tileLayer: TileLayer): TiledMapStage.TiledCellAgglomerate? {
    return this.filter{it.tileLayer == tileLayer}.firstOrNull()
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