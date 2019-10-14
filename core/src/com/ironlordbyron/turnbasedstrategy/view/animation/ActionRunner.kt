package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.CameraMovementAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.GameCameraProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.Rumbler
import javax.inject.Inject
import javax.inject.Singleton

// BASE CASE:  This is the last item on the list, in which case, the action simply attaches itself to the clickListeningActor and we exit.
// RECURSIVE CASE:  We create a sequence composed of the current action, and an instance of this attached to the next index.
// the new TriggerActionAfterCurrentAction (if applicable).
@Singleton
public class ActionRunner @Inject constructor (val rumbler: Rumbler,
                                               val cameraProvider: GameCameraProvider,
                                               val cameraMovementAnimationGenerator: CameraMovementAnimationGenerator){

    var processing: Boolean = false
    var currentActorActionPair: ActorActionPair? = null

    val eventNofifier by LazyInject(EventNotifier::class.java)
    public fun runThroughActionQueue(actionQueue: List<ActorActionPair>,
                                     currentIndex: Int = 0,
                                     interleaveCameraActions: Boolean = true,
                                     finalAction : () -> Unit = {}) {
        if (interleaveCameraActions){
            val finalActionQueue = interleaveActionQueueWithCameraMovements(actionQueue)
            runThroughFinalActionQueue(finalActionQueue, currentIndex, finalAction)
        }else{
            runThroughFinalActionQueue(actionQueue, currentIndex, finalAction)
        }
    }

    public fun continuousPoll(actionQueue: ArrayList<ActorActionPair>){
        if (actionQueue.isEmpty() || processing) return

        processing = true
        var currentAction = actionQueue.first()
        currentActorActionPair = currentAction
        actionQueue.removeAt(0)

        val cameraMovementAction = cameraMovementAnimationGenerator.generateCameraMovementActionToLookAt(currentAction.actor)
        cameraMovementAction.actionOnceAnimationCompletes = {
            processSingleAction(currentAction){
                processing = false
                currentActorActionPair = null
            }
        }
        processSingleAction(cameraMovementAction)
    }

    private fun processSingleAction(currentAction: ActorActionPair, afterward: () -> Unit = {}) {
        var current = currentAction
        if (current.screenShake) {
            rumbler.executeRumble(.5f, 1f)
        }
        current.actor.isVisible = current.startsVisible
        var customAction = CustomAction {
            if (current.name != null) {
                // println("Actor ${current.name} has started processing.")
            }
            if (current.murderActorsOnceCompletedAnimation) {
                current.actor.remove()
                for (pair in current.secondaryActions) {
                    if (pair.murderActorsOnceCompletedAnimation) {
                        pair.actor.remove()
                    }
                }
            }
            if (current.name != null) {
                // println("Actor ${current.name} has finished processing.")
            }
            current.actionOnceAnimationCompletes()
            afterward()
        }
        current.actor.addAction(Actions.sequence(
                current.action,
                customAction
                ))
        for (secondaryPair in current.secondaryActions) {
            secondaryPair.actor.addAction(secondaryPair.action)
        }
    }

    private fun interleaveActionQueueWithCameraMovements(actionQueue: List<ActorActionPair>): List<ActorActionPair> {
        val finalActionQueue = ArrayList<ActorActionPair>()
        for (i in 0 .. actionQueue.size - 1){
            if (actionQueue[i].cameraTrigger){
                val action = actionQueue[i]
                val actorToLookAt = action.cameraFocusActor ?: action.actor
                finalActionQueue.add(cameraMovementAnimationGenerator.generateCameraMovementActionToLookAt(actorToLookAt))
            }
            finalActionQueue.add(actionQueue[i])
        }
        return finalActionQueue
    }

    private fun runThroughFinalActionQueue(actionQueue: List<ActorActionPair>, currentIndex: Int = 0,
                                     finalAction : () -> Unit = {}) {



        if (currentIndex == actionQueue.size) {
            finalAction.invoke()
            currentActorActionPair = null
            return
        }
        val current = actionQueue[currentIndex]
        currentActorActionPair = current
        if (current.screenShake){
            rumbler.executeRumble(.5f, 1f)
        }
        current.actor.isVisible = current.startsVisible
        current.actor.addAction(Actions.sequence(current.action, CustomAction {
            if (current.name != null){
                // println("Actor ${current.name} has started processing.")
            }
            runThroughFinalActionQueue(actionQueue, currentIndex + 1, finalAction)
            if (current.murderActorsOnceCompletedAnimation){
                current.actor.remove()
                for (pair in current.secondaryActions){
                    if (pair.murderActorsOnceCompletedAnimation){
                        pair.actor.remove()
                    }
                }
            }
            if (current.name != null){
                // println("Actor ${current.name} has finished processing.")
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