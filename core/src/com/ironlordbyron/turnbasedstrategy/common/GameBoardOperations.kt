package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.GameDataProvider

/**
 * Responsible for coordinating game-level actions between lower-level actors like the tile map operations handler
 * and the character image processor.
 */
class GameBoardOperator(val tileMapOperationsHandler: TileMapOperationsHandler,
                        gameDataProvider: GameDataProvider){

}