package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AnimationActionQueueProvider @Inject constructor(val actionRunner: ActionRunner) {
    private var actionQueue = ArrayList<ActorActionPair>()
    private val logicHooks by LazyInject(LogicHooks::class.java)

    public fun runThroughActionQueue(finalAction: () -> Unit = {}){
        actionRunner.runThroughActionQueue(actionQueue, finalAction = finalAction)
        clearQueue()
    }

    public fun addAction(actorActionPair: ActorActionPair){
        actionQueue.add(actorActionPair)
    }

    public fun addBareAction(actor: Actor, action : () -> Unit){
        actionQueue.add(ActorActionPair(actor, CustomAction(action)))
    }

    public fun addActions(actorActionPairs: List<ActorActionPair>){
        actionQueue.addAll(actorActionPairs)
    }
    public fun clearQueue(){
        actionQueue = ArrayList()
    }

    public class CustomAction(val action : () -> Unit) : Action() {
        override fun act(delta: Float): Boolean {
            action.invoke()
            return true
        }

    }
}
