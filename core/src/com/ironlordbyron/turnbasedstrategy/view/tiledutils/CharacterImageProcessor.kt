package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.TiledTexturePath
import javax.inject.Inject
import javax.inject.Singleton

data class BoundingRectangle(val x: Int, val y: Int, val width: Int, val height: Int)

fun TiledMapTile.getBounds(): BoundingRectangle {
    return BoundingRectangle(this.textureRegion.regionX, this.textureRegion.regionY, this.textureRegion.regionWidth, this.textureRegion.regionHeight)
}
interface CanTransformTextureToActor<out T> {
    fun placeSprite(tiledMap: TiledMap, tileLocation: TileLocation, texture: TextureRegion) : T
}

public fun TiledMapTileLayer.getBoundsOfTile(tileLocation: TileLocation): BoundingRectangle {
    val width = Math.round(this.tileWidth)
    val height = Math.round(this.tileHeight)
    val x = width * tileLocation.x
    val y = height * tileLocation.y
    return BoundingRectangle(x.toInt(), y.toInt(), width.toInt(), height.toInt())
}

private val TextureRegion.boundingRectangle: BoundingRectangle
    get() {
        return BoundingRectangle(this.regionX, this.regionY, this.regionWidth, this.regionHeight)
    }

@Singleton
class SpriteActorFactory @Inject constructor(val stageProvider: TacticalTiledMapStageProvider) {
    fun createSpriteActor(texture: TextureRegion, bounds: BoundingRectangle, alpha: Float = 1f): SpriteActor {
        val spriteActor = SpriteActor(texture, bounds, alpha)

        stageProvider.tiledMapStage.addActor(spriteActor)
        spriteActor.toFront()
        return spriteActor
    }

    fun createSpriteActorForTile(tiledMap: TiledMap, location: TileLocation, textureRegion: TextureRegion,
                                 alpha: Float = 1f): SpriteActor {
        val boundingBox = (tiledMap.layers[0] as TiledMapTileLayer).getBoundsOfTile(location)
        return createSpriteActor(textureRegion, boundingBox, alpha)
    }

}

class SpriteActor(val texture: TextureRegion, var bounds: BoundingRectangle,
                  var alpha: Float = 1f) : Image(texture) {
    init {
        x = bounds.x.toFloat()
        y = bounds.y.toFloat()
        addAction(Actions.alpha(alpha))
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }

}