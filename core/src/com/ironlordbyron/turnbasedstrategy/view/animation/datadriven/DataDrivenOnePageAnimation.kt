package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


/**
 * Represents animations where it's just a spritesheet.
 */
data class DataDrivenOnePageAnimation(val filePath: String, val rows : Int, val cols: Int): ProtoActor {
    override fun toActor(animatedImageParams: AnimatedImageParams): AnimatedImage {
        return AnimatedImage.fromDataDrivenAnimation(this, animatedImageParams)
    }

    companion object {
        val EXPLODE = DataDrivenOnePageAnimation("animations/exp2.png", 4, 4)
        val FIRE = DataDrivenOnePageAnimation("animations/Fire.png", 3, 3)
    }
}
