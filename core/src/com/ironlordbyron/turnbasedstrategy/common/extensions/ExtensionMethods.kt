package com.ironlordbyron.turnbasedstrategy.common.extensions

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image


public fun Texture.toImage(): Image {
    val img = Image(this)
    return img
}