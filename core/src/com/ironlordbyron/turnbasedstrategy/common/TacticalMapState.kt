package com.ironlordbyron.turnbasedstrategy.common

import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTile
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalTileTracker
import javax.inject.Singleton

/**
 * Responsible for doing very basic game rule enforcement
 */
@Singleton
class TacticalMapState @Inject constructor(val logicalTileTracker: LogicalTileTracker){

    val locations = HashMap<TileLocation, TacticalMapTileState>()
    val listOfCharacters = ArrayList<LogicalCharacter>()

    val listOfEnemyCharacters: List<LogicalCharacter>
    get() = listOfCharacters.filter{!it.playerControlled}


    fun init(){
        for (tile in logicalTileTracker.tiles){
            locations.put(tile.location, TacticalMapTileState(tile.location, true))
        }
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
}

public fun <E> java.util.HashSet<E>.doesNotContain(it: E): Boolean {
    return !this.contains(it)
}

data class TacticalMapTileState(val tileLocation: TileLocation,
                                val passable: Boolean)

