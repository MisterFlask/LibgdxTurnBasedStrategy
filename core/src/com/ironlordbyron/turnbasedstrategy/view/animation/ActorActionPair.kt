package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor


data class ActorActionPair(val actor: Actor, val action: Action,
                           /**
                            * Secondary actions are actions that are run at the same time as
                            * the action given above, EXCEPT that their finishing is not considered
                            * a signal to move on to the next actor in the queue.
                            * As of initial implementation, this is used so that we can have multiple tile highlights
                            * work within our queueing architecture.
                            */
                           val secondaryActions: Collection<ActorActionPair> = ArrayList(),
                           val murderActorsOnceCompletedAnimation: Boolean = false,
                           val name: String? = null) {

}