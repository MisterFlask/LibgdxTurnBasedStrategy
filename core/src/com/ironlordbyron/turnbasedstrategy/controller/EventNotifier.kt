package com.ironlordbyron.turnbasedstrategy.controller

import com.google.inject.ImplementedBy
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

public interface GameEventListener{
    fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent)
}

@ImplementedBy(EventNotifier::class)
interface GameEventNotifier{
    fun notifyListenersOfGameEvent(tacticalGameEvent: TacticalGameEvent)
    fun registerGameListener(gameEventListener: GameEventListener)
}

@Singleton
public class EventNotifier() : GameEventNotifier{
    fun registerGuiListener(eventListener: EventListener){
        if (listeners.contains(eventListener)){
            return
        }
        this.listeners += eventListener
    }

    override fun registerGameListener(gameEventListener: GameEventListener){
        if (gameEventListeners.contains(gameEventListener)){
            return
        }
        gameEventListeners.add(gameEventListener)
    }
    val gameEventListeners = ArrayList<GameEventListener>()
    val listeners = ArrayList<EventListener>()
    /**
     * Used to notify the CONTROLLERs of things happening in the VIEW
     */
    fun notifyListenersOfGuiEvent(tacticalGuiEvent: TacticalGuiEvent){
        for (listener in listeners){
            listener.consumeGuiEvent(event = tacticalGuiEvent)
        }
    }

    // Used to notify the VIEW of things happening in the GAME
    override fun notifyListenersOfGameEvent(tacticalGameEvent: TacticalGameEvent){
        for (listener in gameEventListeners){
            listener.consumeGameEvent(tacticalGameEvent)
        }
    }
}

public interface TacticalGameEvent{
    data class UnitKilled(val character: LogicalCharacter) : TacticalGameEvent
    data class EntityDamage(val tileEntity: TileEntity, val ability: LogicalAbility) : TacticalGameEvent
    data class UnitTurnStart(val unit: LogicalCharacter) : TacticalGameEvent
}
