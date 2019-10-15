package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tileentity.CityTileEntity.Companion.name
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


    private var currentlyMovingCamera = false
    var lastAnimationTime = 0f;
    var startedAnimation = false
    var queueModeActive = true

    public fun continuousPoll(actionQueue: MutableList<ActorActionPair>){
        if (!queueModeActive){
            launchAllActionsFromQueue(actionQueue)
            return
        }

        val currentAap  = currentActorActionPair
        if (currentAap != null && currentAap.actor.stage == null){
            println("Actor  ${currentAap.actor.name} is no longer attached to a stage; removing from queue and continuing.")
            currentActorActionPair = null
            processing = false
        }
        if (actionQueue.isEmpty() || processing) return

        processNextActionInQueue(actionQueue, true)
    }

    private fun launchAllActionsFromQueue(actionQueue: MutableList<ActorActionPair>) {
        while (!actionQueue.isEmpty()){
            processNextActionInQueue(actionQueue, false)
        }
    }

    private fun processNextActionInQueue(actionQueue: MutableList<ActorActionPair>, queueModeActive: Boolean) {
        processing = true
        var currentAction = actionQueue.first()
        actionQueue.removeAt(0)

        currentlyMovingCamera = true
        val cameraMovementAction = cameraMovementAnimationGenerator.generateCameraMovementActionToLookAt(currentAction.cameraFocusActor
                ?: currentAction.actor)
        currentActorActionPair = currentAction

        currentAction.actor.addAction(Actions.sequence(
                CustomAction {
                    startedAnimation = true
                },
                cameraMovementAction.action,
                CustomAction {
                    currentlyMovingCamera = false
                    processSingleAction(currentAction) {
                        processing = false
                        startedAnimation = false
                        if (queueModeActive){
                            continuousPoll(actionQueue)
                        }
                    }
                }
        ))
    }

    private fun processSingleAction(currentAction: ActorActionPair, afterward: () -> Unit = {}) {
        val current = currentAction
        if (current.screenShake) {
            rumbler.executeRumble(.5f, 1f)
        }
        current.actor.isVisible = current.startsVisible
        val customAction = CustomAction {
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
            println("Actor ${current.name} has finished processing; performing post-action step")
            current.actionOnceAnimationCompletes()
            afterward()
        }
        current.actor.addAction(Actions.sequence(
                CustomAction{
                    println("Actor ${current.name} has started processing")
                },
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