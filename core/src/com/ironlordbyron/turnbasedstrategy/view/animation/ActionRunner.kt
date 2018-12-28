package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.Rumbler
import javax.inject.Inject
import javax.inject.Singleton

// BASE CASE:  This is the last item on the list, in which case, the action simply attaches itself to the actor and we exit.
// RECURSIVE CASE:  We create a sequence composed of the current action, and an instance of this attached to the next index.
// the new TriggerActionAfterCurrentAction (if applicable).
@Singleton
public class ActionRunner @Inject constructor (val rumbler: Rumbler){
    public fun runThroughActionQueue(actionQueue: List<ActorActionPair>, currentIndex: Int = 0,
                                     finalAction : () -> Unit = {}) {



        if (currentIndex == actionQueue.size) {
            finalAction.invoke()
            return
        }
        val current = actionQueue[currentIndex]
        if (current.screenShake){
            rumbler.executeRumble(.5f, 1f)
        }
        current.actor.addAction(Actions.sequence(current.action, CustomAction {
            if (current.name != null){
                println("Actor ${current.name} has started processing.")
            }
            runThroughActionQueue(actionQueue, currentIndex + 1, finalAction)
            if (current.murderActorsOnceCompletedAnimation){
                current.actor.remove()
                for (pair in current.secondaryActions){
                    pair.actor.remove()
                }
            }
            if (current.name != null){
                println("Actor ${current.name} has finished processing.")
            }
            current.actionOnceAnimationCompletes()
        }))
        for (secondaryPair in current.secondaryActions){
            secondaryPair.actor.addAction(secondaryPair.action)
        }
    }
}


private class CustomAction(val execution: ()->Unit): Action() {
    override fun act(delta: Float): Boolean {
        execution.invoke()
        return true
    }
}