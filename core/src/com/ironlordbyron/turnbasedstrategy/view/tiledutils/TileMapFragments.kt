package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.*
import com.google.inject.Singleton
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TILE_SIZE
import java.util.*
import javax.inject.Inject


data class TacticalTileMap(val tileMap: TiledMap,
                           val strategyTileMap: TacticalTileMap)

/**
 * Responsible for ensuring tilemaps remain singletons.
 */
@Singleton
class TileMapOperationsHandler @Inject constructor(val logicalTileTracker: LogicalTileTracker) {
    val mapCache = HashMap<String, TiledMap>()

    fun copyFragmentTo(gameMapName: String,
                       minX: Int,
                       minY: Int,
                       fragmentName: String) {
        val gameMapLayer = pullTileMapLayer(gameMapName, isFragment = false, tileLayer = TileLayer.FEATURE)
        val fragmentLayer = pullTileMapLayer(fragmentName, isFragment = true, tileLayer = TileLayer.BASE)
        copyTo(gameMapLayer,
                fragmentLayer,
                minX,
                minY)
    }

    fun pullTextureFromTilemap(tileMapWithTextureName: String, textureId: String, tileSetWithTexture: String) : TextureRegion{
        val map = getTileMapFromFullyQualifiedName(tileMapWithTextureName)

        val tiledMapTileSets = map.tileSets
        val set = tiledMapTileSets.getTileSet(tileSetWithTexture)
        val tile = set.getTile(Integer.valueOf(textureId))


        val textureRegion = tile.textureRegion
        return textureRegion
    }

    fun getTileMapFromFullyQualifiedName(key: String): TiledMap{
        if (!mapCache.contains(key)){
            val map = TmxMapLoader()
                    .load(key)
            mapCache[key] = map
        }
        return mapCache[key]!!
    }

    fun getPossiblePlayerSpawnPositions(map: TiledMap) : Collection<TileLocation>{
        return map.getTilesInObject("PLAYER_SPAWN")

    }



    fun getTileMap(name: String, isFragment: Boolean): TiledMap{
        val precursor = if (isFragment) fragmentsPrecursor else sourceMapPrecursor
        val key = "${precursor}/${name}"
        return getTileMapFromFullyQualifiedName(key)
    }

    fun pullTileMapLayer(name: String, isFragment: Boolean, tileLayer: TileLayer): TiledMapTileLayer {
        return getTileMap(name, isFragment)
                .getTileLayer(tileLayer)
    }

    private fun copyTo(gameMapLayer: TiledMapTileLayer,
                       fragmentMapLayer: TiledMapTileLayer,
                       minX: Int, minY: Int) {
        for (x in minX..fragmentMapLayer.width + minX) {
            for (y in minY..fragmentMapLayer.height + minY) {
                val fragmentX = x - minX
                val fragmentY = y - minY
                val fragmentCell = fragmentMapLayer.getCell(fragmentX, fragmentY)
                if (fragmentCell != null){
                    gameMapLayer.setCell(x, y, fragmentCell)

                }
            }
        }
    }
}

enum class TileLayer{
    BASE, FEATURE,

    CHARACTER_IMAGES
}

fun TiledMap.getObjectLayerRectangles(): List<LogicalTiledObject>{
    val layer = this.layers["ObjectLayer"]
    val rectangles = layer.objects.getByType(RectangleMapObject::class.java)
    val logicalobjects = ArrayList<LogicalTiledObject>()
    for (rec in rectangles){
        val logicalTiledObject = LogicalTiledObject(Math.round(rec.rectangle.x), Math.round(rec.rectangle.y),
                Math.round(rec.rectangle.width), Math.round(rec.rectangle.height),
                rec.name)
        logicalobjects.add(logicalTiledObject)
    }
    return logicalobjects
}

fun TiledMap.getTilesInObject(name: String): List<TileLocation>{
    val objectLayerRectangles = this.getObjectLayerRectangles()
    val rec = objectLayerRectangles.first{it.name == name}
    val tiles = ArrayList<TileLocation>()
    for (x in (rec.x / TILE_SIZE) until (rec.x + rec.width)/TILE_SIZE){
        for (y in (rec.y/TILE_SIZE) until (rec.y + rec.height)/TILE_SIZE){
            tiles.add(TileLocation(x, y))
        }
    }
    return tiles
}

data class LogicalTiledObject(val x: Int, val y: Int, val width: Int, val height: Int, val name: String)


fun TiledMap.getTileLayer(layer: TileLayer) : TiledMapTileLayer{

   val num = when (layer){
       TileLayer.BASE -> "TerrainLayer"
       TileLayer.FEATURE -> "TerrainFeatures"
       TileLayer.CHARACTER_IMAGES -> "CharacterImages"
   }
    var mapLayer = this.layers[num]
    if (mapLayer == null){
        return this.layers[0] as TiledMapTileLayer
    }
    return mapLayer as TiledMapTileLayer
}

private val fragmentsPrecursor = "tilesets/fragments"
private val sourceMapPrecursor = "tilesets"

object TileMapFragment {


    val City = "CityFragment.tmx"

}

fun TiledMapTileLayer.getTiles() : Set<TiledMapTile>{
    return this.getTiles()
}