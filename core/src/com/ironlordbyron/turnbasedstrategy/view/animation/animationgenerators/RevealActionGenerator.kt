package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor

public class RevealActionGenerator(){
    fun generateRevealAction(actor: Actor) : Action {
        return RevealAction(actor)
    }
}

private class RevealAction(val actorToReveal: Actor) : Action(){
    init{
        actorToReveal.isVisible = false
    }

    override fun act(delta: Float): Boolean {
        actorToReveal.isVisible = true
        return true
    }

}