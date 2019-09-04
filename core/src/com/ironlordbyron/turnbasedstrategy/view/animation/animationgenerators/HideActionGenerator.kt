package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair

public class HideAnimationGenerator(){
    fun generateHideAction(actor: Actor) : Action {
        return HideAction(actor)
    }
    fun generateHideActorActionPair(actor: Actor, cameraFocusActor: Actor? = null) : ActorActionPair {
        return ActorActionPair(actor, HideAction(actor), cameraFocusActor = cameraFocusActor)
    }
}

private class HideAction(val actorToHide: Actor) : Action(){
    override fun act(delta: Float): Boolean {
        actorToHide.isVisible = false
        return true
    }

}