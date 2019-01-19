package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.animation.external.LineEffect
import java.util.*
import javax.inject.Singleton

@Singleton
public class TransientEntityTracker{

    val lines = ArrayList<LineEffect>()
    val actors = HashMap<UUID, Actor>()

    fun retrieveActorByUuid(uuid: UUID) : Actor? {
        return actors.get(uuid)
    }

    fun retrieveLineEffectByUuid(uuid: UUID): LineEffect {
        return lines.first{it.guid == uuid}
    }

    fun insertLine(lineEffect: LineEffect){
        lines.add(lineEffect)
    }
    fun insertActor(actor: Actor): UUID {
        val uuid = UUID.randomUUID()
        actors.put(uuid, actor)
        return uuid
    }


}