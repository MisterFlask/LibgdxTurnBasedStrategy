package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.BoundingRectangle
import com.ironlordbyron.turnbasedstrategy.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.getBoundsOfTile
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TileMapProvider {
    lateinit var tiledMap: TiledMap

    fun getBoundingBoxOfTile(tileLocation: TileLocation): BoundingRectangle {
        return (tiledMap.layers[0] as TiledMapTileLayer).getBoundsOfTile(tileLocation)
    }

}


@Singleton
class TempBattleStarter @Inject constructor(val boardProvider: TileMapProvider,
                                            val gameBoardOperator: GameBoardOperator,
                                            val tiledMapInterpreter: TiledMapInterpreter){
    fun startBattle(){
        val legitTiles = tiledMapInterpreter.getPossiblePlayerSpawnPositions(boardProvider.tiledMap)
        for (tile in legitTiles){
            gameBoardOperator.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.DEFAULT_UNIT, tileLocation = tile,
                    playerControlled = true)
        }
        val enemyTiles = tiledMapInterpreter.getPossibleEnemySpawnPositions(boardProvider.tiledMap)
        for (tile in enemyTiles){
            gameBoardOperator.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.DEFAULT_ENEMY_UNIT, tileLocation = tile,
                    playerControlled = false)
        }
    }
}