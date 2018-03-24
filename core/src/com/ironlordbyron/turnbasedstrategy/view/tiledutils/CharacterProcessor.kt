package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import javax.inject.Inject
import javax.inject.Singleton

data class BoundingRectangle(val x: Int, val y: Int, val width: Int, val height: Int)

fun TiledMapTile.getBounds(): BoundingRectangle {
    return BoundingRectangle(this.textureRegion.regionX, this.textureRegion.regionY, this.textureRegion.regionWidth, this.textureRegion.regionHeight)
}

data class LogicalCharacter(val actor: SpriteActor, var tileLocation: TileLocation)

class CharacterImageManager @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                                                val characterActorFactory: CharacterActorFactory,
                                                val logicalTileTracker: LogicalTileTracker) {

    private val characterSpriteSheet = "tilesets/Player0Characters.tmx"

    fun getCharacterSprite(): TextureRegion {
        return tileMapOperationsHandler.pullTextureFromTilemap(characterSpriteSheet, "6", "Player0")
    }

    fun placeCharacterSprite(tiledMap: TiledMap, tileLocation: TileLocation, characterTexture: TextureRegion) {
        val logicalTile = logicalTileTracker.getLogicalTileFromLocation(tileLocation)!!
        var boundingBox = (tiledMap.layers[0] as TiledMapTileLayer).getBoundsOfTile(logicalTile.location)
        // TODO: Fix this, bounding box calcs are wrong
        val characterActor = characterActorFactory.createSpriteActor(characterTexture, boundingBox)

    }
}

private fun TiledMapTileLayer.getBoundsOfTile(tileLocation: TileLocation): BoundingRectangle {
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
class CharacterActorFactory @Inject constructor() {
    lateinit var stage: Stage
    fun createSpriteActor(texture: TextureRegion, bounds: BoundingRectangle): SpriteActor {
        val spriteActor = SpriteActor(texture, bounds)

        stage.addActor(spriteActor)
        spriteActor.toFront()
        return spriteActor
    }
}

class SpriteActor(texture: TextureRegion, var bounds: BoundingRectangle) : Image(texture) {
    init {
        x = bounds.x.toFloat()
        y = bounds.y.toFloat()
    }

}