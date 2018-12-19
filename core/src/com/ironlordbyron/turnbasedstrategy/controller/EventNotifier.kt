package com.ironlordbyron.turnbasedstrategy.controller

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
    // Represents the user having seen the unit get hit
    class CameraShouldRumble() : TacticalGameEvent

}
