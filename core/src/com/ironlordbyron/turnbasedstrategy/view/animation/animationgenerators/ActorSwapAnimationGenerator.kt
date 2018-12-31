package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.getBoundingBox
import com.ironlordbyron.turnbasedstrategy.tiledutils.setBoundingBox
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject

public class ActorSwapAnimationGenerator @Inject constructor(val stageProvider: TacticalTiledMapStageProvider){

    public fun generateActorSwapActorActionPair(protoActorToCreate: ProtoActor,
                                                animatedImageParams: AnimatedImageParams,
                                                actorSettable: ActorSettable
                                                ) : ActorActionPair{
        val actorToReplace = actorSettable.actor
        val actorToReveal = protoActorToCreate.toActor(animatedImageParams)
        actorToReveal.isVisible = false
        stageProvider.tiledMapStage.addActor(actorToReveal)
        actorToReveal.setBoundingBox(actorSettable.actor.getBoundingBox())
        return ActorActionPair(actorToReplace, ActorSwapAction(actorToReveal, actorToReplace, actorSettable))

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