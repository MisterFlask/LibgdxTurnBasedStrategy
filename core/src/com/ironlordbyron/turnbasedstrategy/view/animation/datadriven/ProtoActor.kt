package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ShadeableActor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


public interface ProtoActor{
    val orientation: OrientationType
    fun toActor(animatedImageParams: AnimatedImageParams = AnimatedImageParams.RUN_ALWAYS_AND_FOREVER) : ShadeableActor
    fun getDefaultAnimatedImageParams() : AnimatedImageParams? {
        return null
    }
}
