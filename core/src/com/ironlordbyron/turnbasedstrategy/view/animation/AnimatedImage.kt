package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation


class AnimatedImage(val animation: Animation<TextureRegion>, val animatedImageParams: AnimatedImageParams) : Image(animation.getKeyFrame(0f)) {
    init{
        this.isVisible = animatedImageParams.startsVisible
    }
    fun activate() {
        if (alreadyActivated){
            return
        }
        this.stateTime = 0f
        this.isVisible = true //this isn't getting called before act() is.
        this.alreadyActivated = true
    }

    private var alreadyActivated: Boolean = false;
    private var stateTime = 0f

    init{
        animation.playMode = Animation.PlayMode.NORMAL
    }

    override fun act(delta: Float) {
        stateTime += delta
        (getDrawable() as TextureRegionDrawable).setRegion(animation.getKeyFrame(stateTime, animatedImageParams.loops))
        super.act(delta)
    }
    companion object {
        fun fromDataDrivenAnimation(dataDrivenOnePageAnimation: DataDrivenOnePageAnimation,
                                    animatedImageParams: AnimatedImageParams): AnimatedImage {
            return AnimatedImage(SpriteSheetParser().createAnimation(dataDrivenOnePageAnimation), animatedImageParams)
        }

        fun fromTextureRegions(textures: List<TextureRegion>, animatedImageParams: AnimatedImageParams): AnimatedImage {
            return AnimatedImage(Animation<TextureRegion>(animatedImageParams.frameDuration, textures.toLibgdxArray()), animatedImageParams)
        }

    }
}


fun <T> Collection<T>.toLibgdxArray() : Array<T> {
  val walkFrames = Array<T>(this.size)
    this.forEach{walkFrames.add(it)}
    return walkFrames
}

data class AnimatedImageParams(
                               val startsVisible: Boolean = false,
                               val loops : Boolean = false,
                               val frameDuration: Float){
    companion object {
        val RUN_ONCE_AFTER_DELAY = AnimatedImageParams(startsVisible = false, loops = false, frameDuration = 0.25f)
        val RUN_ALWAYS_AND_FOREVER = AnimatedImageParams(true, true, frameDuration = 0.25f)
    }
}