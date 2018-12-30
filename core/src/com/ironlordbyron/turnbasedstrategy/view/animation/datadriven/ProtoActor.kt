package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


public interface ProtoActor{
    fun toActor(animatedImageParams: AnimatedImageParams) : Actor
    fun getDefaultAnimatedImageParams() : AnimatedImageParams? {
        return null
    }
}
