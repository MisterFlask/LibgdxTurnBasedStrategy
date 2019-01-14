package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image

class ImageNotRespectingClicks(pullGenericTexture: TextureRegion) : Image(pullGenericTexture) {

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }
}
class GroupNotRespectingClicks() : Group(){
    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }
}