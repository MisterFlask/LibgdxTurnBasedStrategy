package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ActionQueueProvider @Inject constructor(val actionRunner: ActionRunner) {
    private var actionQueue = ArrayList<ActorActionPair>()

    public fun runThroughActionQueue(finalAction: () -> Unit = {}){
        actionRunner.runThroughActionQueue(actionQueue, finalAction = finalAction)
    }

    public fun addAction(actorActionPair: ActorActionPair){
        actionQueue.add(actorActionPair)
    }

    public fun clearQueue(){
        actionQueue = ArrayList()
    }
}
