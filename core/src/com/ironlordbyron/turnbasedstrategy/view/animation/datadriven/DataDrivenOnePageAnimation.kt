package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


/**
 * Represents animations where it's just a spritesheet.
 */
data class DataDrivenOnePageAnimation(val filePath: String, val rows : Int, val cols: Int, override val orientation: OrientationType = OrientationType.NEUTRAL): ProtoActor {
    override fun toActor(animatedImageParams: AnimatedImageParams): AnimatedImage {
        return AnimatedImage.fromDataDrivenAnimation(this, animatedImageParams)
    }

    companion object {
        val EXPLODE = DataDrivenOnePageAnimation("animations/exp2.png", 4, 4)
        val FIRE = DataDrivenOnePageAnimation("animations/Fire.png", 3, 3)
        val FIREBALL = DataDrivenOnePageAnimation("animations/fireball.png", 2, 3, OrientationType.LEFT)
    }
}

enum class OrientationType(val degrees: Int? = null) {
    NEUTRAL,
    LEFT(270),
    RIGHT(60),
    UP(0),
    DOWN(180)
}
