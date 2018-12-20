package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


public interface ProtoAnimation{
    fun toAnimatedImage(animatedImageParams: AnimatedImageParams) : AnimatedImage
}
