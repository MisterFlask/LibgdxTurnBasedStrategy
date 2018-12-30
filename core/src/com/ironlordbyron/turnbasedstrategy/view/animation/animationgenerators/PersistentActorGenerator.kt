package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject

public class PersistentActorGenerator @Inject constructor(){
    private val defaultAnimatedImageParams = AnimatedImageParams(startsVisible = true, loops = true, frameDuration = .04f)

    public fun createPersistentActor(protoActor: ProtoActor,
                                     animatedImageParams: AnimatedImageParams? = null,
                                     alphaOverride: Float? = null) : Actor {
        var animatedImageParams = animatedImageParams?:defaultAnimatedImageParams
        if (alphaOverride != null){
            animatedImageParams = animatedImageParams.copy(alpha = alphaOverride)
        }
        val actor = protoActor.toActor(animatedImageParams)
        return actor
    }
}