package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


/**
 * Represents animations where it's just a spritesheet.
 */
data class DataDrivenOnePageAnimation(val filePath: String, val rows : Int, val cols: Int,
                                      override val orientation: OrientationType = OrientationType.NEUTRAL,
                                      val frameDurationInSeconds: Float = .25f,
                                      val scaleFactor: Float = 1f): ProtoActor {
    override fun toActorWrapper(animatedImageParams: AnimatedImageParams): AnimatedImage {
        return AnimatedImage.fromDataDrivenAnimation(this, animatedImageParams)
    }

    companion object {
        val SNOOZE_ACTOR = DataDrivenOnePageAnimation("animations/snooze_animation.png", 7, 10,
                scaleFactor = 1f,
                frameDurationInSeconds = .01f)
        val RED_SHIELD_ACTOR = DataDrivenOnePageAnimation("animations/ring_red.png", 3, 6,
                scaleFactor = 2f,
                frameDurationInSeconds = .01f)
        val EXPLODE = DataDrivenOnePageAnimation("animations/exp2.png", 4, 4,
                frameDurationInSeconds = .04f)
        val FIRE = DataDrivenOnePageAnimation("animations/Fire.png", 3, 3)
        val LASER = DataDrivenOnePageAnimation("animations/laser.png", 3, 6,
                frameDurationInSeconds = .03f)
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
