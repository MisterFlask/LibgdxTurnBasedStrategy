package com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen

/**
 * Created by Aaron on 3/21/2018.
 */
enum class TiledMapObject(val tiledRepresentation: String) {
    PLAYER_SPAWN("PLAYER_SPAWN")


}

enum class TiledMapLayers(val tiledName: String) {
    OBJECT_LAYER("ObjectLayer")
}

val TILE_SIZE = 16