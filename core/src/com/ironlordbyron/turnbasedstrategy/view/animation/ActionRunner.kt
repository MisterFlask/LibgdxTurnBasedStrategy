package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

// This "action" does two things.
// 1: Creates a new "TriggerActionAfterCurrentAction" for the NEXT action in the list,
// assuming this item is not the last in the list.
// 2:  Adds a sequence to the NEXT actor in the list of the action provided, and THEN,
// BASE CASE:  This is the last item on the list, in which case, the action simply attaches itself to the actor and we exit.
// RECURSIVE CASE:  We create a sequence composed of the current action, and an instance of this attached to the next index.
// the new TriggerActionAfterCurrentAction (if applicable).
public class ActionRunner {
    public fun runThroughActionQueue(actionQueue: List<ActorActionPair>, currentIndex: Int = 0,
                                     finalAction : () -> Unit = {}) {

        val current = actionQueue[currentIndex]
        if (currentIndex == actionQueue.size - 1) {
            // we're at the end of the list.
            current.actor.addAction(current.action)
            finalAction.invoke()
            return
        }
        current.actor.addAction(Actions.sequence(current.action, CustomAction {
            runThroughActionQueue(actionQueue, currentIndex + 1)
        }))
    }
}
private class CustomAction(val execution: ()->Unit): Action() {
    override fun act(delta: Float): Boolean {
        execution.invoke()
        return true
    }
}