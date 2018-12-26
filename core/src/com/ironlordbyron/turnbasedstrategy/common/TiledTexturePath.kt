package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.xml.TilemapXmlProcessor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.ImageNotRespectingClicks
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps.Companion.COMMON_TILE_MAP


object TileSetName{
    val PLAYER = "Player0" // TODO
}

data class TiledTexturePath(
        val spriteId: String,
        val tileSetName: String,
        val sourceTileMapName: String = COMMON_TILE_MAP
): ProtoActor {
    override fun toActor(animatedImageParams: AnimatedImageParams): Actor {
        return ImageNotRespectingClicks(TiledMapOperationsHandler(TilemapXmlProcessor()).pullGenericTexture(spriteId, tileSetName))
    }

    companion object {
        val RED_TILE = TiledTexturePath("0", "red_tile")
        val BLUE_TILE = TiledTexturePath("0", "blue_tile")
        val GREEN_TILE = TiledTexturePath("0", "green_tile")
    }
}
