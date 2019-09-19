package com.ironlordbyron.turnbasedstrategy.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.fasterxml.jackson.databind.ObjectMapper


public enum class ActorOrdering(val order: Int){
    TILE (0),
    TILE_FEATURE(5),
    UNIT(10),
    HIGHLIGHTS(15),
    FOG_OF_WAR(30)

}

public data class ActorName(val ordering: ActorOrdering? = null)
val objmapper = ObjectMapper()

public fun Actor.setFunctionalName(name: ActorName){
    this.name = objmapper.writeValueAsString(name)
}
public fun Actor.getFunctionalName() : ActorName{
    if (this.name == null){
        return ActorName(null)
    }
    return objmapper.readValue(this.name, ActorName::class.java)
}

public class ActorSortOrderComparator() : Comparator<Actor>{
    override fun compare(o1: Actor?, o2: Actor?): Int {
        if (o1 == null && o2 == null) return 0
        if (o1 == null) return -1
        if (o2 == null) return 1
        val order1 = o1.getFunctionalName().ordering?.order?:-1
        val order2 = o2.getFunctionalName().ordering?.order?:-1
        return order1.compareTo(order2)
    }
}