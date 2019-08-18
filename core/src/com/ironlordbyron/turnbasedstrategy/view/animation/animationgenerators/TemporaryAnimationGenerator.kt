package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.StageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.SpriteSheetParser
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
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
                                                       val tiledMapStageProvider: StageProvider,
                                                       val spriteSheetParser: SpriteSheetParser
                                  ) {
    // WARNING: This doesn't actually work with protoactors that don't create AnimatedImages
    public fun getTemporaryAnimationActorActionPair(tileLocation: TileLocation, dataDrivenOnePageAnimation: ProtoActor): ActorActionPair {
        val animatedImage = dataDrivenOnePageAnimation.toActor(animatedImageParams = AnimatedImageParams.RUN_ONCE_AFTER_DELAY) as AnimatedImage // HACK
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
