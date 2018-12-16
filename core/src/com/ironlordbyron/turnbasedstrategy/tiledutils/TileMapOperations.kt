package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.*
import com.google.inject.Singleton
import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.xml.TilemapXmlProcessor
import java.util.*
import javax.inject.Inject


data class TacticalTileMap(val tileMap: TiledMap,
                           val strategyTileMap: TacticalTileMap)

/**
 * Responsible for ensuring tilemaps remain singletons,
 * and handling direct tilemap instructions.
 * NOT for business logic.
 */
@Singleton
class TileMapOperationsHandler @Inject constructor(val logicalTileTracker: LogicalTileTracker,
                                                   val xmlParser: TilemapXmlProcessor) {
    val mapCache = HashMap<String, TiledMap>()

    fun copyFragmentTo(gameMapName: String,
                       minX: Int,
                       minY: Int,
                       fragmentName: String) {
        val gameMapLayer = pullTileMapLayer(gameMapName, MapType.SOURCE_MAP, tileLayer = TileLayer.FEATURE)
        val fragmentLayer = pullTileMapLayer(fragmentName, MapType.FRAGMENT_MAP, tileLayer = TileLayer.BASE)
        copyTo(gameMapLayer,
                fragmentLayer,
                minX,
                minY)
    }

    fun pullGenericTexture(textureId: String, tileSetName: String) : TextureRegion{
        return pullTextureFromTilemap(CharacterTemplates.CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE, textureId, tileSetName)
    }

    fun pullTextureFromTilemap(tileMapWithTextureName: String, textureId: String, tileSetWithTexture: String): TextureRegion {
        val map = getTileMapFromFullyQualifiedName(tileMapWithTextureName)

        val tiledMapTileSets = map.tileSets
        val set = tiledMapTileSets.getTileSet(tileSetWithTexture) ?: throw IllegalStateException("Could not find set at map $tileMapWithTextureName, set $tileSetWithTexture")
        val firstgid = xmlParser.getTilesetFirstgid(tileMapWithTextureName, tileSetWithTexture)
        val tile = set.getTile(Integer.valueOf(textureId) + Integer.valueOf(firstgid)) ?: throw IllegalStateException("Could not find tile at map $tileMapWithTextureName, set $tileSetWithTexture, id $textureId ." +
                "Only keys found were ${set.asIterable().map{it.id}}")

        val textureRegion = tile.textureRegion
        return textureRegion
    }

    fun getTileMapFromFullyQualifiedName(key: String): TiledMap {
        if (!mapCache.contains(key)) {
            val map = TmxMapLoader()
                    .load(key)
            mapCache[key] = map
        }
        return mapCache[key]!!
    }

    fun getPossiblePlayerSpawnPositions(map: TiledMap): Collection<TileLocation> {
        return map.getTilesInObjectByType("PLAYER_SPAWN")
    }
    fun getPossibleEnemySpawnPositions(map: TiledMap) : Collection<TileLocation> {
        return map.getTilesInObjectByType("ENEMY_SPAWN")
    }


    fun getTileMap(name: String, mapType: MapType): TiledMap {
        val precursor = mapType.filePrefix
        val key = "$precursor/$name"
        return getTileMapFromFullyQualifiedName(key)
    }

    fun pullTileMapLayer(name: String, mapType: MapType, tileLayer: TileLayer): TiledMapTileLayer {
        return getTileMap(name, mapType)
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
                if (fragmentCell != null) {
                    gameMapLayer.setCell(x, y, fragmentCell)

                }
            }
        }
    }
}

enum class TileLayer(val layerName: String) {
    BASE("TerrainLayer"),
    FEATURE("TerrainFeatures"),
    CHARACTER_IMAGES("CharacterImages"),
    TILE_HIGHLIGHT_LAYER_RED("TileHighlightLayerRed"),
    TILE_HIGHLIGHT_LAYER_BLUE("TileHighlightLayerBlue"),
    TILE_HIGHLIGHT_LAYER_GREEN("TileHighlightLayerGreen");

    companion object{
        fun getTileLayerFromName(s : String): TileLayer? {
            return TileLayer.values().firstOrNull{it.layerName == s}
        }
    }
}


fun TiledMap.getObjectLayerRectangles(): List<LogicalTiledObject> {
    val layer = this.layers["ObjectLayer"]
    val rectangles = layer.objects.getByType(RectangleMapObject::class.java)
    val logicalobjects = ArrayList<LogicalTiledObject>()
    for (rec in rectangles) {

        val logicalTiledObject = LogicalTiledObject(
                Math.round(rec.rectangle.x),
                Math.round(rec.rectangle.y),
                Math.round(rec.rectangle.width),
                Math.round(rec.rectangle.height),
                rec.name, rec.properties, rec.properties["type"] as String)
        logicalobjects.add(logicalTiledObject)
    }
    return logicalobjects
}

//TODO
const val TILE_SIZE = 16

data class TiledObjectIdentifier(val boundingRectangle: BoundingRectangle, val properties : Map<String, String>)


fun TiledMap.getTilesInObjectByType(type: String): List<TileLocation> {
    val objectLayerRectangles = this.getObjectLayerRectangles()
    if (!objectLayerRectangles.any{it.properties["type"] == type}){
        throw IllegalArgumentException("unable to find tiles matching type of " + type)
    }
    val rec = objectLayerRectangles.first { it.properties["type"] == type }
    val tiles = ArrayList<TileLocation>()
    for (x in (rec.x / TILE_SIZE) until (rec.x + rec.width) / TILE_SIZE) {
        for (y in (rec.y / TILE_SIZE) until (rec.y + rec.height) / TILE_SIZE) {
            tiles.add(TileLocation(x, y))
        }
    }
    return tiles
}

data class LogicalTiledObject(val x: Int, val y: Int, val width: Int, val height: Int, val name: String,
                              val properties: MapProperties, val type: String)

fun TiledMap.getTileLayer(layer: TileLayer): TiledMapTileLayer {

    val name = layer.layerName
    val mapLayer = this.layers[name] ?: return this.layers[0] as TiledMapTileLayer
    return mapLayer as TiledMapTileLayer
}

private const val fragmentsPrecursor = "tilesets/fragments"
private const val sourceMapPrecursor = "tilesets"

object TileMapFragment {


    val City = "CityFragment.tmx"

}

fun TiledMapTileLayer.getTiles(): Set<TiledMapTile> {
    return this.getTiles()
}

enum class MapType(val filePrefix: String){
    SOURCE_MAP(sourceMapPrecursor), FRAGMENT_MAP(fragmentsPrecursor), HIGHLIGHT_MAP("tilehighlights")
}