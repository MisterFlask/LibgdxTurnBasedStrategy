package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.utils.Array
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject



class AnimationAppearTemporarily(val animation: AnimatedImage) : Action(){

    var currentTime = 0f
    override fun act(delta: Float): Boolean {
        animation.activate()
        currentTime+=delta
        if (animation.animation.isAnimationFinished(currentTime)){
            return true
        }
        return false
    }
}

class ActorAppearTemporarily(val mainActor: ActivatableActor,
                             val durationSeconds: Float) : Action(){

    var currentTime = 0f
    override fun act(delta: Float): Boolean {
        mainActor.activateIfInactive()
        currentTime+=delta
        if (currentTime > durationSeconds){
            return true
        }
        return false
    }
}

public interface ActivatableActor {
    public fun activateIfInactive()
}

public interface CanTransformIntoAnimatedImage{
    fun toAnimatedImage(animatedImageParams: AnimatedImageParams) : AnimatedImage
}

/**
 * Represents animations where it's just a spritesheet.
 */
data class DataDrivenOnePageAnimation(val filePath: String, val rows : Int, val cols: Int): CanTransformIntoAnimatedImage{
    override fun toAnimatedImage(animatedImageParams: AnimatedImageParams): AnimatedImage {
        return AnimatedImage.fromDataDrivenAnimation(this, animatedImageParams)
    }

    companion object {
        val EXPLODE = DataDrivenOnePageAnimation("animations/exp2.png", 4, 4)
    }
}

data class OpposedSpritesheets(val spritesheets: Collection<String>, val spritesheetX: Int, val spritesheetY: Int){

}


class AnimationParser(){
    public fun createAnimation(anim: DataDrivenOnePageAnimation): Animation<TextureRegion> {
        val frameRows = anim.rows
        val frameCols = anim.cols
        val walkSheet = Texture(Gdx.files.internal(anim.filePath))

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        val tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / frameCols,
                walkSheet.getHeight() / frameRows)

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        val walkFrames = Array<TextureRegion>(frameCols * frameRows)
        for (i in 0 until frameRows) {
            for (j in 0 until frameCols) {
                walkFrames.add(tmp[i][j])
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        val walkAnimation = Animation<TextureRegion>(0.025f, walkFrames)
        return walkAnimation
    }
}

class TemporaryAnimationGenerator @Inject constructor (val tileMapProvider: TileMapProvider,
                                                       val tiledMapStageProvider: TacticalTiledMapStageProvider,
                                                       val animationParser: AnimationParser
                                  ) {
    val FRAME_ROWS = 4
    val FRAME_COLS = 4
    public fun getTemporaryAnimationActorActionPair(tileLocation: TileLocation, dataDrivenOnePageAnimation: DataDrivenOnePageAnimation): ActorActionPair{
        val walkAnimation = animationParser.createAnimation(dataDrivenOnePageAnimation)
        val animatedImage = AnimatedImage(walkAnimation, AnimatedImageParams.RUN_ONCE_AFTER_DELAY)
        tiledMapStageProvider.tiledMapStage.addActor(animatedImage)
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        animatedImage.setX(boundingBox.x.toFloat())
        animatedImage.setY(boundingBox.y.toFloat())
        animatedImage.width = boundingBox.width.toFloat()
        animatedImage.height = boundingBox.height.toFloat()
        return ActorActionPair(actor = animatedImage,
                action = AnimationAppearTemporarily(animatedImage),
                name = "Animation appearing temporarily",
                murderActorsOnceCompletedAnimation = true)
    }

}
