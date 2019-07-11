package com.ironlordbyron.turnbasedstrategy.common.extensions

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.view.animation.ImageWrapper


public fun Texture.toImage(): Image {
    val img = Image(this)
    return img
}

public fun TextureRegion.toNonHittableImage(): Image{
    return ImageWrapper((this), false)
}