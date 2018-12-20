package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.xml.TilemapXmlProcessor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


data class SuperimposedTilemaps(val tileMapWithTextureName: String = COMMON_TILE_MAP,
                                val tileSetNames: List<String>,
                                val textureId: String) : ProtoAnimation {

    override fun toAnimatedImage(animatedImageParams: AnimatedImageParams): AnimatedImage {
        return AnimatedImage.fromTextureRegions(TiledMapOperationsHandler(TilemapXmlProcessor()).pullTextures(this),
                animatedImageParams)
    }

    companion object {
        val PLAYER_TILE_SETS = listOf("Player0", "Player1")
        val COMMON_TILE_MAP = "tilesets/Player0Characters.tmx"
    }

}