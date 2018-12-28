package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.Ability
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import tiled.core.Tile
import javax.inject.Singleton

/**
 * Created by Aaron on 3/24/2018.
 */
public interface EventListener{
    fun consumeGuiEvent(event: TacticalGuiEvent){

    }

    fun consumeGameEvent(event : TacticalGameEvent){

    }
}

@Singleton
public class EventNotifier(){
    fun registerGuiListener(eventListener: EventListener){
        this.listeners += eventListener
    }

    val listeners = ArrayList<EventListener>()
    fun notifyListenersOfGuiEvent(tacticalGuiEvent: TacticalGuiEvent){
        for (listener in listeners){
            listener.consumeGuiEvent(event = tacticalGuiEvent)
        }
    }

    fun notifyListenersOfGameEvent(tacticalGameEvent: TacticalGameEvent){
        for (listener in listeners){
            listener.consumeGameEvent(event = tacticalGameEvent)
        }
    }
}

public interface TacticalGameEvent{
    data class UnitSpawned(val character: LogicalCharacter) : TacticalGameEvent
    data class EntityDamage(val tileEntity: TileEntity, val ability: LogicalAbility) : TacticalGameEvent

}
