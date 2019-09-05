package com.ironlordbyron.turnbasedstrategy.common.wrappers

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN

fun Actor.addSimpleTooltip(str: String){
    TooltipManager.getInstance().maxWidth = 200f
    val tooltip = TextTooltip(str, DEFAULT_SKIN)
    tooltip.setInstant(true)
    tooltip.setAlways(true)
    this.addListener(tooltip)
}


public interface ActorWrapper{
    val actor: Actor
    var shader: ShaderProgram?

    public fun addTooltip(renderingFunction: RenderingFunction){
        TooltipManager.getInstance().maxWidth = 200f
        val tooltip = TextTooltip(renderingFunction.render(), DEFAULT_SKIN)
        tooltip.setInstant(true)
        tooltip.setAlways(true)
        actor.addListener(tooltip)
    }

}

class SimpleTextRenderer(val string: String) : RenderingFunction{
    override fun render(): String {
        return string
    }
}

public interface RenderingFunction{
    fun render() : String

    companion object {
        fun simple(str: String): RenderingFunction {
            val renderer : RenderingFunction = SimpleTextRenderer(str)
            return renderer
        }
    }
}
