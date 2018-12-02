package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import javax.inject.Singleton

/**
 * Created by Aaron on 3/24/2018.
 */
public interface EventListener{
    fun consumeEvent(event: TacticalGuiEvent)
}

@Singleton
public class EventNotifier(){
    fun registerListener(eventListener: EventListener){
        this.listeners += eventListener
    }

    val listeners = ArrayList<EventListener>()
    fun notifyListeners(tacticalGuiEvent: TacticalGuiEvent){
        for (listener in listeners){
            listener.consumeEvent(event = tacticalGuiEvent)
        }
    }
}


sealed class TacticalGuiEvent{
    data class CharacterSelected(val character: LogicalCharacter) : TacticalGuiEvent()
    data class TileClicked(val tileLocation: TileLocation) : TacticalGuiEvent()
    class EndTurnButtonClicked() : TacticalGuiEvent()
    class FinishedEnemyTurn(): TacticalGuiEvent()
}