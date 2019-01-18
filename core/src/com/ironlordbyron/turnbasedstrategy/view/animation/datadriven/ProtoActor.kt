package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


public interface ProtoActor{
    val orientation: OrientationType
    fun toActor(animatedImageParams: AnimatedImageParams = AnimatedImageParams.RUN_ALWAYS_AND_FOREVER) : ActorWrapper
    fun getDefaultAnimatedImageParams() : AnimatedImageParams? {
        return null
    }
}
