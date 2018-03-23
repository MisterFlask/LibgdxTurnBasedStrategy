package com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameDataProvider {
    lateinit var tiledMap: TiledMap
}


@Singleton
class BattleStarter @Inject constructor(val boardProvider: GameDataProvider,
                    val characterImageManager: CharacterImageManager,
                    val tileMapOperationsHandler: TileMapOperationsHandler){
    fun startBattle(){
        val legitTiles = tileMapOperationsHandler.getPossiblePlayerSpawnPositions(boardProvider.tiledMap)
        for (tile in legitTiles){
            characterImageManager.placeCharacterSprite(boardProvider.tiledMap, tile, characterImageManager.getCharacterSprite())
        }
    }
}