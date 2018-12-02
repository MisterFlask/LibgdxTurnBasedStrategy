package com.ironlordbyron.turnbasedstrategy.common


/**
 * All tile textures are assumed to be contained within Player0Characters.tmx
 */
data class TiledTexturePath(
        val spriteId: String,
        val tileSetName: String = "Player0" //Default path name
)
object TiledTexturePaths {
    val RED_TILE = TiledTexturePath("0", "red_tile")
    val BLUE_TILE = TiledTexturePath("0", "blue_tile")
    val GREEN_TILE = TiledTexturePath("0", "green_tile")
}
