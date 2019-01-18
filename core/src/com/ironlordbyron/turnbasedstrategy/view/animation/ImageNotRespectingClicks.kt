package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ShadeableActor

class ImageNotRespectingClicks(pullGenericTexture: TextureRegion) : Image(pullGenericTexture), ShadeableActor {
    override val actor: Actor get() = this
    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }
}
class LogicalCharacterActorGroup(val shadeableActor: ShadeableActor,
                                 val characterActor: Actor = shadeableActor.actor) : Group(){
    var hpMarker: Label? = null

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }
}