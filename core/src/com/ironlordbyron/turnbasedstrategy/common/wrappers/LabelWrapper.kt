package com.ironlordbyron.turnbasedstrategy.common.wrappers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin

public interface LabelWrapper : ActorWrapper{
    val label: Label
}

public class LabelWrapperImpl(val text: String,
                              style:LabelStyle, val hittable: Boolean = true) : Label(text, style), LabelWrapper{
    override val label: Label get() = this
    override val actor: Actor get() = this

    constructor(text:String, skin: Skin) : this(text,  skin.get(LabelStyle::class.java))

    override var shader : ShaderProgram? = null

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.shader = shader
        super.draw(batch, parentAlpha)
        batch?.shader = null
    }
}