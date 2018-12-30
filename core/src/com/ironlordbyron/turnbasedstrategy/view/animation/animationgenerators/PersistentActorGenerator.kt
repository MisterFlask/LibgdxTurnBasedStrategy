package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject

public class PersistentActorGenerator @Inject constructor(){
    private val defaultAnimatedImageParams = AnimatedImageParams(startsVisible = true, loops = true, frameDuration = .04f)

    public fun createPersistentActor(protoActor: ProtoActor, animatedImageParams: AnimatedImageParams? = null) : Actor {
        val actor = protoActor.toActor(animatedImageParams?:defaultAnimatedImageParams)
        return actor
    }
}