package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper

class ImageNotRespectingClicks(pullGenericTexture: TextureRegion) : Image(pullGenericTexture), ActorWrapper {
    override var shader: ShaderProgram? = null
    override val actor: Actor get() = this
    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }
}
class LogicalCharacterActorGroup(val shadeableActor: ActorWrapper,
                                 val characterActor: Actor = shadeableActor.actor) : Group(){
    var hpMarker: Label? = null

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }
}