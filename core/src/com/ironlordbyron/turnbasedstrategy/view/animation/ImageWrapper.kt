package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.images.Icon

class ImageWrapper(texture: TextureRegion,
                   val hittable: Boolean = false) : Image(texture), ActorWrapper {
    override var shader: ShaderProgram? = null
    override val actor: Actor get() = this
    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (hittable){
            return super.hit(x, y, touchable)
        }else{
            super.hit(x, y, touchable)
            return null
        }
    }
}
class LogicalCharacterActorGroup(val shadeableActor: ActorWrapper,
                                 val characterActor: Actor = shadeableActor.actor,
                                 val attributeActors: HashMap<String, Actor> = HashMap()) : Group(){
    var hpMarker: Label? = null

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
    }
}


fun Actor.setTrueColor(color: Color){
    if (this is Group){
        for (child in this.children){
            child.color = color
        }
    }else{
        this.color = color
    }
}