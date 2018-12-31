package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject

public class PersistentActorGenerator @Inject constructor(val revealActionGenerator: RevealActionGenerator){
    private val defaultAnimatedImageParams = AnimatedImageParams(startsVisible = true, loops = true)

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

    /**
     * Creates an actor action pair that just results in the actor appearing to the player.
     */
    public fun createPersistentActorActorActionPair(protoActor: ProtoActor,
                                        animatedImageParams: AnimatedImageParams? = null,
                                        alphaOverride: Float? = null) : ActorActionPair {
        val actor = createPersistentActor(protoActor, animatedImageParams, alphaOverride)
        return ActorActionPair(actor, revealActionGenerator.generateRevealAction(actor))
    }
}