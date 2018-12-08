package com.ironlordbyron.turnbasedstrategy.common.extensions

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.BoundingRectangle
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActor


public fun Texture.toImage(): Image {
    val img = Image(this)
    return img
}