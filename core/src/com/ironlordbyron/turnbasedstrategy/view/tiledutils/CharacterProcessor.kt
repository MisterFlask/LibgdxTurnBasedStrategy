package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Actor
import javax.inject.Inject
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

data class BoundingRectangle(val x: Int, val y: Int, val width: Int, val height: Int)

fun TiledMapTile.getBounds() : BoundingRectangle{
    return BoundingRectangle(this.textureRegion.regionX, this.textureRegion.regionY, this.textureRegion.regionWidth, this.textureRegion.regionHeight)
}

data class LogicalCharacter(val texture: TextureRegion)

class CharacterImageManager @Inject constructor (val tileMapOperationsHandler: TileMapOperationsHandler){

    private val characterSpriteSheet = "tilesets/Player0Characters.tmx"

    fun getCharacterSprite(): TextureRegion {
        return tileMapOperationsHandler.pullTextureFromTilemap(characterSpriteSheet, "6", "Player0")
    }

    fun placeCharacterSprite(tiledMap: TiledMap, tileLocation: TileLocation, characterTexture: TextureRegion){
        val tile = StaticTiledMapTile(characterTexture)
        val cell = TiledMapTileLayer.Cell()

        cell.tile = tile
        tiledMap.getTileLayer(TileLayer.CHARACTER_IMAGES).setCell(tileLocation.x, tileLocation.y, cell)
    }
}

// hah
class CharacterActor(val texture: TextureRegion) : Actor() {

    init{
        this.setBounds()
    }
}