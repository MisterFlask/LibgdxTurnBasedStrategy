package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.KnownObjectType

data class LogicalTile(val terrainTile: TiledMapTile,
                       val location: TileLocation,
                       val clickListeningActor: TileMapActor,
                       val allTilesAtThisSquare: List<TiledMapStage.TiledCellAgglomerate>,
                       var terrainType: TerrainType = TerrainType.UNINITIALIZED,

                       val markers: List<KnownObjectType> = listOf()) {
    lateinit var fogOfWarTileActor: Actor
    var underFogOfWar: Boolean = true

    fun tileHasProperty(property: String) : Boolean{
        // basically, if ANY_CHARACTER cell has a property on this tile we return true regardless of what layer it's in.
        return allTilesAtThisSquare.any{it.cellHasProperty(property)}
    }

    fun isTerrainMountainous(): Boolean {
        return layerHasBooleanPropertySetToTruthy(TileLayer.FEATURE, "mountain")
    }

    val terrainTypeFromUnderlyingTile: TerrainType
        get() = {
            if (isTerrainMountainous()) TerrainType.MOUNTAIN
            else TerrainType.GRASS
        }()

    fun layerHasBooleanPropertySetToTruthy(layer: TileLayer, property: String): Boolean {
        val prop = allTilesAtThisSquare
                .firstOrNull { it.tileLayer == layer }
                ?.tiledCell?.tile?.properties?.get(property)
        if (prop == null) {
            return false
        }
        if (prop is Boolean) {
            return prop
        } else {
            return true
        }
    }

}