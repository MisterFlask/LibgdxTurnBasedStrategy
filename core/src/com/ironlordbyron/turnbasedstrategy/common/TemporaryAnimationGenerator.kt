package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject


class AnimatedImage(val animation: Animation<TextureRegion>) : Image(animation.getKeyFrame(0f)) {
    init{
        this.isVisible = false
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
        (getDrawable() as TextureRegionDrawable).setRegion(animation!!.getKeyFrame(stateTime, false))
        super.act(delta)
    }
}

class AppearTemporarily(val animation: AnimatedImage) : Action(){

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

class TemporaryAnimationGenerator @Inject constructor (val tileMapProvider: TileMapProvider,
                                                       val tiledMapStageProvider: TacticalTiledMapStageProvider
                                  ) {
    val FRAME_ROWS = 4
    val FRAME_COLS = 4
    public fun getTemporaryAnimationActorActionPair(tileLocation: TileLocation): ActorActionPair{
        val walkSheet = Texture(Gdx.files.internal("animations/exp2.png"))

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        val tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS)

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        val walkFrames = Array<TextureRegion>(FRAME_COLS * FRAME_ROWS)
        for (i in 0 until FRAME_ROWS) {
            for (j in 0 until FRAME_COLS) {
                walkFrames.add(tmp[i][j])
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        val walkAnimation = Animation<TextureRegion>(0.025f, walkFrames)
        val animatedImage = AnimatedImage(walkAnimation)
        tiledMapStageProvider.tiledMapStage.addActor(animatedImage)
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        animatedImage.setX(boundingBox.x.toFloat())
        animatedImage.setY(boundingBox.y.toFloat())
        animatedImage.width = boundingBox.width.toFloat()
        animatedImage.height = boundingBox.height.toFloat()
        return ActorActionPair(actor = animatedImage,
                action = AppearTemporarily(animatedImage),
                name = "Animation appearing temporarily",
                murderActorsOnceCompletedAnimation = true)
    }
}
