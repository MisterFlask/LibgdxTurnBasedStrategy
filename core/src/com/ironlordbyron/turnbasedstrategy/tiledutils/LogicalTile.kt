package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.TileLocation


data class LogicalTile(val terrainTile: TiledMapTile,
                       val location: TileLocation,
                       val actor: TileMapActor,
                       val cell: TiledMapTileLayer.Cell,
                       val allTilesAtThisSquare: List<TiledMapStage.TiledCellAgglomerate>,
                       var terrainType: TerrainType = TerrainType.GRASS) {

    fun isTerrainMountainous(): Boolean {
        return layerHasBooleanPropertySetToTrue(TileLayer.FEATURE, "mountain")
    }

    val terrainTypeFromUnderlyingTile: TerrainType
        get() = {
            if (isTerrainMountainous()) TerrainType.MOUNTAIN
            else TerrainType.GRASS
        }()

    fun layerHasBooleanPropertySetToTrue(layer: TileLayer, property: String): Boolean {
        val prop = allTilesAtThisSquare
                .firstOrNull { it.tileLayer == layer }
                ?.tiledCell?.tile?.properties?.get(property)
        if (prop == null) {
            return false
        }
        if (prop is Boolean) {
            return prop
        } else {
            throw IllegalStateException("Property $property should be a boolean, but it's a ${prop.javaClass.name}")
        }
    }
}