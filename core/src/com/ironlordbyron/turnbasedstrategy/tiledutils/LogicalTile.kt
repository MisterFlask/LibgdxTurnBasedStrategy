package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.KnownObjectType


data class LogicalTile(val terrainTile: TiledMapTile,
                       val location: TileLocation,
                       val actor: TileMapActor,
                       val allTilesAtThisSquare: List<TiledMapStage.TiledCellAgglomerate>,
                       var terrainType: TerrainType = TerrainType.GRASS,
                       val markers: List<KnownObjectType> = listOf()) {

    fun tileHasProperty(property: String) : Boolean{
        // basically, if ANY cell has a property on this tile we return true regardless of what layer it's in.
        return allTilesAtThisSquare.any{it.cellHasProperty(property)}
    }

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