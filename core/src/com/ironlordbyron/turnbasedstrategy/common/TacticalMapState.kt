package com.ironlordbyron.turnbasedstrategy.common

import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import javax.inject.Singleton

/**
 * Responsible for doing tracking what tiles contain what characters.
 */
@Singleton
class TacticalMapState @Inject constructor(val logicalTileTracker: LogicalTileTracker,
                                           val eventNotifier: EventNotifier) : GameEventListener{

    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is TacticalGameEvent.INITIALIZE -> {
                // TODO: Remove locations list, is inappropriate
                locations.clear()
                listOfCharacters.clear()
            }
        }
    }
    init {
        eventNotifier.registerGameListener(this)
    }

    private val locations = HashMap<TileLocation, TacticalMapTileState>()
    val listOfCharacters = ArrayList<LogicalCharacter>()

    val listOfEnemyCharacters: List<LogicalCharacter>
    get() = listOfCharacters.filter{!it.playerControlled}


    fun init(){
        for (tile in logicalTileTracker.tiles){
            locations.put(tile.location, TacticalMapTileState(tile.location, true))
        }
    }

    fun addCharacter(logicalCharacter: LogicalCharacter){
        if (listOfCharacters.any{it.tileLocation == logicalCharacter.tileLocation}){
            throw IllegalArgumentException("Attempted to add character to a tile with another character already created")
        }
        listOfCharacters.add(logicalCharacter)
    }


    fun characterAt(tile: TileLocation): LogicalCharacter?{
        return listOfCharacters.filter{it.tileLocation == tile}.firstOrNull()
    }

    fun moveCharacterToTile(character: LogicalCharacter, tile: TileLocation){
        character.tileLocation = tile
        character.actionsLeft -= 1
        if (character.actionsLeft == 0){
            character.endedTurn = true
        }
    }

    fun isDoorAt(it: TileLocation): Boolean {
        return logicalTileTracker.isDoor(it)
    }
}

public fun <E> java.util.HashSet<E>.doesNotContain(it: E): Boolean {
    return !this.contains(it)
}

data class TacticalMapTileState(val tileLocation: TileLocation,
                                val passable: Boolean)

