package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler


class HighlightType(val tiledTexturePath: TiledTexturePath,
                    val color: Color? = null) {

    val tiledMapOperationsHandler: TiledMapOperationsHandler by lazy{
        GameModuleInjector.generateInstance(TiledMapOperationsHandler::class.java)
    }


    fun toTexture() : TextureRegion {
        val texture = tiledMapOperationsHandler.pullGenericTexture(
                this.tiledTexturePath.spriteId,
                this.tiledTexturePath.tileSetName)
        return texture
    }

    companion object{
        val RED_TILE = HighlightType(TiledTexturePath.WHITE_TILE, Color.RED)
        val BLUE_TILE = HighlightType(TiledTexturePath.WHITE_TILE, Color.BLUE)
        val GREEN_TILE = HighlightType(TiledTexturePath.WHITE_TILE, Color.GREEN)
        val WHITE_TILE = HighlightType(TiledTexturePath.WHITE_TILE)
        // special-purpose
        val ENEMY_MOVE_TILE = HighlightType(TiledTexturePath.WHITE_TILE, Color.MAGENTA)
        val ENEMY_ATTACK_TILE = HighlightType(TiledTexturePath.WHITE_TILE, Color.FIREBRICK)

        fun tileOfColor(color: Color) : HighlightType {
            return HighlightType(TiledTexturePath.WHITE_TILE, color)
        }
    }

}
