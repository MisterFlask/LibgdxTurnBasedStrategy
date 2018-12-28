package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.ImageNotRespectingClicks

class ActorFromTiledTextureRegion(cell: TiledMapTileLayer.Cell){
    lateinit var imageActor: Image
    val originalTile = cell.tile
    init{
        acquireImageActor()
        cell.setTile(null)
    }

    fun acquireImageActor(){
        imageActor = ImageNotRespectingClicks(originalTile.textureRegion)
    }
}
