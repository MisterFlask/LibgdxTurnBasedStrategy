package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.tiledutils.StageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.getBoundingBox
import com.ironlordbyron.turnbasedstrategy.tiledutils.setBoundingBox
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject

public class ActorSwapAnimationGenerator @Inject constructor(val stageProvider: StageProvider){

    @Deprecated("I don't think this still works")
    public fun generateActorSwapActorActionPair(protoActorToCreate: ProtoActor,
                                                animatedImageParams: AnimatedImageParams,
                                                originalActor: ActorSettable
                                                ) : ActorActionPair{
        val actorToReplace = originalActor.actor
        val actorToReveal = protoActorToCreate.toActorWrapper(animatedImageParams).actor
        actorToReveal.isVisible = false
        stageProvider.tiledMapStage.addActor(actorToReveal)
        // TODO: I think this is because of our usage of groups that causes coordinates to be set differently
        //  from where they used to
        actorToReveal.setBoundingBox(originalActor.actor.getBoundingBox())
        return ActorActionPair(actorToReplace, ActorSwapAction(actorToReveal, actorToReplace, originalActor))
    }
}


private class ActorSwapAction(val actorToReveal: Actor,
                              val actorToReplace: Actor,
                              val actorSettable: ActorSettable) : Action(){
    init{
        actorToReveal.isVisible = false
    }

    override fun act(delta: Float): Boolean
    {
        actorToReveal.isVisible = true
        actorToReplace.isVisible = false
        actorToReplace.remove()
        actorSettable.actor = actorToReveal
        return true
    }

}