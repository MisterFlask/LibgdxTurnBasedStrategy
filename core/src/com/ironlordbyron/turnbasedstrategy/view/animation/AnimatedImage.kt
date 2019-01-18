package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation


interface ScaledActor{
    val scalingFactor: Float
}

class AnimatedImage(val animation: Animation<TextureRegion>, val animatedImageParams: AnimatedImageParams, override val scalingFactor: Float) : Image(animation.getKeyFrame(0f)),
ScaledActor, ActorWrapper {

    override var shader: ShaderProgram? = null
    init{
        this.isVisible = animatedImageParams.startsVisible
        this.color.a = animatedImageParams.alpha
    }

    override val actor: Actor get() = this

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        super.hit(x, y, touchable)
        return null
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

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.shader = shader
        super.draw(batch, parentAlpha)
        batch?.shader = null
    }

    companion object {
        fun fromDataDrivenAnimation(dataDrivenOnePageAnimation: DataDrivenOnePageAnimation,
                                    animatedImageParams: AnimatedImageParams): AnimatedImage {
            return AnimatedImage(SpriteSheetParser.INSTANCE.createAnimation(dataDrivenOnePageAnimation, dataDrivenOnePageAnimation.frameDurationInSeconds), animatedImageParams,
                    dataDrivenOnePageAnimation.scaleFactor)
        }

        fun fromTextureRegions(textures: List<TextureRegion>, animatedImageParams: AnimatedImageParams): AnimatedImage {
            // todo: Don't hardcode frame duration
            return AnimatedImage(Animation<TextureRegion>(.25f, textures.toLibgdxArray()), animatedImageParams, scalingFactor = 1f)
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
                               val alpha: Float = 1f){
    companion object {
        val RUN_ONCE_AFTER_DELAY = AnimatedImageParams(startsVisible = false, loops = false)
        val RUN_ALWAYS_AND_FOREVER = AnimatedImageParams(true, true)
    }
}