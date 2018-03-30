package com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TileMapProvider {
    lateinit var tiledMap: TiledMap
}


@Singleton
class BattleStarter @Inject constructor(val boardProvider: TileMapProvider,
                                        val gameBoardOperator: GameBoardOperator,
                                        val tileMapOperationsHandler: TileMapOperationsHandler){
    fun startBattle(){
        val legitTiles = tileMapOperationsHandler.getPossiblePlayerSpawnPositions(boardProvider.tiledMap)
        for (tile in legitTiles){
            gameBoardOperator.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.DEFAULT_UNIT, tileLocation = tile)
        }
    }
}