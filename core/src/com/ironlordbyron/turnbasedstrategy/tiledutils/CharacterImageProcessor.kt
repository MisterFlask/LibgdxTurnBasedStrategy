package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject
import javax.inject.Singleton

enum class JustificationType{
    BOTTOM, // encompasses bottom 1/8th of the tile surface.
    TOP,
    TOP_RIGHT // encompasses top 1/8th of the tile surface.
}

data class BoundingRectangle(val x: Int, val y: Int, val width: Int, val height: Int){
    fun getChunkOfBoundingRectangle(numCols: Int, justify: JustificationType, n: Int): BoundingRectangle {
        if (n >= numCols){
            throw IllegalArgumentException("Can't have an n greater than numCols")
        }
        if (justify == JustificationType.TOP_RIGHT){
            val proportionDedicatedToStuff = 1/4
            val toBeDistributed = this.width * (1 - proportionDedicatedToStuff)
            val x = this.width * toBeDistributed
            val y = this.height * (1 - proportionDedicatedToStuff)
            val widthPerCol = this.width / numCols
            return BoundingRectangle(x + toBeDistributed/widthPerCol * n, y,
                    width= widthPerCol,
                    height = this.height * proportionDedicatedToStuff)
        }else if (justify == JustificationType.BOTTOM || justify == JustificationType.TOP){
            val widthPerCol = this.width / numCols
            val bottomHeight = this.height * 1/4
            val minY : Float = if (justify == JustificationType.BOTTOM) 0f else 3.0f / 4.0f * height
            return BoundingRectangle(n * widthPerCol, minY.toInt(), widthPerCol, bottomHeight)
        }
        throw java.lang.IllegalArgumentException("Gotta choose a justification")
    }

}

fun TiledMapTile.getBounds(): BoundingRectangle {
    return BoundingRectangle(this.textureRegion.regionX, this.textureRegion.regionY, this.textureRegion.regionWidth, this.textureRegion.regionHeight)
}
interface CanTransformTextureToActor {
    fun placeCharacterActor(tileLocation: TileLocation, protoActor: ProtoActor) : Actor
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

    fun createSpriteActorForTile(tiledMap: TiledMap,
                                 location: TileLocation,
                                 textureRegion: TextureRegion,
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