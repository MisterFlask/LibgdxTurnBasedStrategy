package com.ironlordbyron.turnbasedstrategy.common


object TileSetName{
    val PLAYER = "Player0" // TODO
}

data class TiledTexturePath(
        val spriteId: String,
        val tileSetName: String
){

    companion object {
        val RED_TILE = TiledTexturePath("0", "red_tile")
        val BLUE_TILE = TiledTexturePath("0", "blue_tile")
        val GREEN_TILE = TiledTexturePath("0", "green_tile")
    }

    /**
     * Returns the list of tile texture paths corresponding to this item.
     * Assumes exactly two.
     */
    fun toAnimatedTiledTexturePaths(spriteId: String): List<TiledTexturePath> {
        return listOf(
                TiledTexturePath(spriteId, "${tileSetName}0"),
                TiledTexturePath(spriteId, "${tileSetName}1"))
        ;
    }
}
