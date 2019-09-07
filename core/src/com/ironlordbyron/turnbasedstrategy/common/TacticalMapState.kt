package com.ironlordbyron.turnbasedstrategy.common

import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Singleton


public data class TileAlreadyOccupiedException(val tileLocation: TileLocation) : Exception()

/**
 * Responsible for doing tracking what tiles contain what characters.
 */
@Singleton
class TacticalMapState @Inject constructor(val logicalTileTracker: LogicalTileTracker,
                                           val eventNotifier: EventNotifier) : GameEventListener{

    val evacuatedUnits = ArrayList<TacMapUnitTemplate>()
    val unitsAvailableToDeploy = ArrayList<TacMapUnitTemplate>()
    val deadCharacters = ArrayList<TacMapUnitTemplate>()

    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is TacticalGameEvent.INITIALIZE -> {
                locations.clear()
                listOfCharacters.clear()
            }
        }
    }
    init {
        eventNotifier.registerGameListener(this)
    }

    fun evacuateUnit(logicalCharacter: LogicalCharacter){
        if (!listOfCharacters.contains(logicalCharacter)){
            throw IllegalArgumentException("Couldn't find logical character: ${logicalCharacter.tacMapUnit.templateName}")
        }
        listOfCharacters.remove(logicalCharacter)
        evacuatedUnits.add(logicalCharacter.tacMapUnit)
    }

    fun getCharacterFromId(uuid: UUID): LogicalCharacter {
        return this.listOfCharacters.first{it.id == uuid}
    }

    private val locations = HashMap<TileLocation, TacticalMapTileState>()
    val listOfCharacters = ArrayList<LogicalCharacter>()

    val listOfPlayerCharacters: List<LogicalCharacter>
        get() = listOfCharacters.filter{it.playerControlled}

    val listOfEnemyCharacters: List<LogicalCharacter>
    get() = listOfCharacters.filter{!it.playerControlled}


    fun init(){
        for (tile in logicalTileTracker.tiles){
            locations.put(tile.location, TacticalMapTileState(tile.location, true))
        }
    }

    fun addCharacter(logicalCharacter: LogicalCharacter){
        if (listOfCharacters.any{it.tileLocation == logicalCharacter.tileLocation}){
            throw TileAlreadyOccupiedException(logicalCharacter.tileLocation)
        }
        listOfCharacters.add(logicalCharacter)
    }

    fun closestPlayerControlledCharacterTo(logicalCharacter: LogicalCharacter): LogicalCharacter? {
        return listOfPlayerCharacters
                .filter{it.id != logicalCharacter.id}
                .minBy{ it.tileLocation.distanceTo(logicalCharacter.tileLocation)}
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

public fun <E> MutableCollection<E>.removeAndAssert(obj: E){
    if (!this.contains(obj)){
        throw IllegalStateException("Couldn't remove object! ${obj.toString()}")
    }
    this.remove(obj)
}