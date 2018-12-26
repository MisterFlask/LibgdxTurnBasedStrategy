package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.xml.TilemapXmlProcessor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams

/**
 * These represent animations that are basically subsections of several different tilesets.  (e.g. Player0 and Player1
 * in the Dawnlike section.)
 */
data class SuperimposedTilemaps(val tileMapWithTextureName: String = COMMON_TILE_MAP,
                                val tileSetNames: List<String>,
                                val textureId: String) : ProtoActor {

    override fun toActor(animatedImageParams: AnimatedImageParams): AnimatedImage {
        val anim = AnimatedImage.fromTextureRegions(TiledMapOperationsHandler(TilemapXmlProcessor()).pullTextures(this),
                animatedImageParams)
        return anim
    }

    companion object {
        val PLAYER_TILE_SETS = listOf("Player0", "Player1")
        val COMMON_TILE_MAP = "tilesets/Player0Characters.tmx"
    }

}