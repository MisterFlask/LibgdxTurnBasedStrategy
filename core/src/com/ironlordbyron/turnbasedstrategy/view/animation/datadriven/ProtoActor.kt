package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams


public interface ProtoActor{
    val orientation: OrientationType
    fun toActor(animatedImageParams: AnimatedImageParams = getDefaultAnimatedImageParams()) : ActorWrapper
    fun getDefaultAnimatedImageParams() : AnimatedImageParams {
        return AnimatedImageParams.RUN_ALWAYS_AND_FOREVER
    }
    companion object{
        fun fromActor(actor: Actor) : ProtoActor{
            return ProtoActorFromActor(actor)
        }
    }
}

public fun Collection<Actor>.consolidateActors() : Group{
    val actorGroup = Group()
    for (actor in this){
        actorGroup.addActor(actor)
    }
    return actorGroup
}

public class ProtoActorFromActor(val actor: Actor,
                                 override val orientation: OrientationType = OrientationType.NEUTRAL) : ProtoActor{
    override fun toActor(animatedImageParams: AnimatedImageParams): ActorWrapper {
        return SimpleActorWrapper(actor)
    }

}

public class SimpleActorWrapper(override val actor: Actor, override var shader: ShaderProgram? = null) : ActorWrapper