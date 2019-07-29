package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


public interface ProtoActor{
    val orientation: OrientationType
    fun toActor(animatedImageParams: AnimatedImageParams = getDefaultAnimatedImageParams()) : ActorWrapper
    fun getDefaultAnimatedImageParams() : AnimatedImageParams {
        return AnimatedImageParams.RUN_ALWAYS_AND_FOREVER
    }
}

public fun Collection<Actor>.consolidateActors() : Group{
    val actorGroup = Group()
    for (actor in this){
        actorGroup.addActor(actor)
    }
    return actorGroup
}