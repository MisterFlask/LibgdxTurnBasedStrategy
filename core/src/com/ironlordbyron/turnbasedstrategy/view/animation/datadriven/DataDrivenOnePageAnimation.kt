package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


/**
 * Represents animations where it's just a spritesheet.
 */
data class DataDrivenOnePageAnimation(val filePath: String, val rows : Int, val cols: Int,
                                      override val orientation: OrientationType = OrientationType.NEUTRAL,
                                      val frameDurationInSeconds: Float = .25f): ProtoActor {
    override fun toActor(animatedImageParams: AnimatedImageParams): AnimatedImage {
        return AnimatedImage.fromDataDrivenAnimation(this, animatedImageParams)
    }

    companion object {
        val RED_SHIELD_ACTOR = DataDrivenOnePageAnimation("animations/ring_red.png", 3, 6)
        val EXPLODE = DataDrivenOnePageAnimation("animations/exp2.png", 4, 4, frameDurationInSeconds = .04f)
        val FIRE = DataDrivenOnePageAnimation("animations/Fire.png", 3, 3)
        val FIREBALL = DataDrivenOnePageAnimation("animations/fireball.png", 2, 3, OrientationType.LEFT)
        val SWORDSLASH = DataDrivenOnePageAnimation("animations/swordslash.png", 6, 6, OrientationType.LEFT)
        val CLAWSLASH = DataDrivenOnePageAnimation("animations/clawslash.png", 5, 6, OrientationType.LEFT, frameDurationInSeconds = .01f)
    }
}

enum class OrientationType(val degrees: Int? = null) {
    NEUTRAL,
    LEFT(270),
    RIGHT(60),
    UP(0),
    DOWN(180)
}
