package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList


@Singleton
class AnimationActionQueueProvider @Inject constructor(val actionRunner: ActionRunner) {
    private val actionQueue = Collections.synchronizedList(ArrayList<ActorActionPair>()) as MutableList<ActorActionPair>
    private val logicHooks by LazyInject(LogicHooks::class.java)
    private var running = false

    public fun runThroughActionQueue(){

    }

    public fun addAction(actorActionPair: ActorActionPair){
        if (actorActionPair.actor.stage == null){
            throw IllegalArgumentException("No stage used for actor action pair: ${actorActionPair.name}")
        }
        actionQueue.add(actorActionPair)
    }

    public fun addBareAction(actor: Actor, action : () -> Unit){
        if (actor.stage == null){
            throw IllegalArgumentException("No stage used for actor: ${actor.name}")
        }
        actionQueue.add(ActorActionPair(actor, CustomAction(action)))
    }

    public fun addActions(actorActionPairs: List<ActorActionPair>){
        for (action in actorActionPairs){
            addAction(action)
        }
    }
    @Deprecated("")
    public fun clearQueue(){
    }

    fun kickOffQueueIfNotRunning() {
        actionRunner.continuousPoll(actionQueue)
    }

    public class CustomAction(val action : () -> Unit) : Action() {
        override fun act(delta: Float): Boolean {
            action.invoke()
            return true
        }

    }
}
