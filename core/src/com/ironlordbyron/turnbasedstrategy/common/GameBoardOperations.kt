package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.view.tiledutils.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.GameDataProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Responsible for coordinating game-level actions between lower-level actors like the tile map operations handler
 * and the character image processor.
 * Acts as a facade that should not include raw images and such in its interface.
 */
@Singleton
class GameBoardOperator @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                        gameDataProvider: GameDataProvider){

    private val listOfCharacters = ArrayList<LogicalCharacter>()

    fun addCharacter(character: LogicalCharacter){
        listOfCharacters.add(character)
    }

}