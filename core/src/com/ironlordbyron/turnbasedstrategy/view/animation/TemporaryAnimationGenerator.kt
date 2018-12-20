package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Action
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoAnimation
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

class TemporaryAnimationGenerator @Inject constructor (val tileMapProvider: TileMapProvider,
                                                       val tiledMapStageProvider: TacticalTiledMapStageProvider,
                                                       val spriteSheetParser: SpriteSheetParser
                                  ) {
    public fun getTemporaryAnimationActorActionPair(tileLocation: TileLocation, protoAnimation: ProtoAnimation): ActorActionPair{
        val animatedImage = protoAnimation.toAnimatedImage(animatedImageParams = AnimatedImageParams.RUN_ONCE_AFTER_DELAY)
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
