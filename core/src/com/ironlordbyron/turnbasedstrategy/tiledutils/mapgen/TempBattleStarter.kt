package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttributeFactory
import com.ironlordbyron.turnbasedstrategy.common.equipment.StandardEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.tiledutils.BoundingRectangle
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
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
                                            val tiledMapInterpreter: TiledMapInterpreter,
                                            val tacmapState: TacticalMapState,
                                            val functionalCharacterAttributeFactory: FunctionalCharacterAttributeFactory,
                                            val animationActionQueueProvider: AnimationActionQueueProvider,
                                            val entitySpawner: EntitySpawner){
    fun startBattle(){
        val legitTiles = tiledMapInterpreter.getPossiblePlayerSpawnPositions(boardProvider.tiledMap)
        for (tile in legitTiles){
            entitySpawner.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.DEFAULT_UNIT, tileLocation = tile,
                    playerControlled = true).let{
                it.equipment.add(StandardEquipment.sword)
                it.equipment.add(StandardEquipment.flamethrower)
            }
        }
        val enemyTiles = tiledMapInterpreter.getPossibleEnemySpawnPositions(boardProvider.tiledMap)
        for (tile in enemyTiles){
            entitySpawner.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.DEFAULT_ENEMY_UNIT, tileLocation = tile,
                    playerControlled = false).let{
                it.equipment.add(StandardEquipment.sword)
            }
        }
        val spawners = tiledMapInterpreter.getSpawners(boardProvider.tiledMap)
        for (tile in spawners){
            entitySpawner.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.DEFAULT_ENEMY_UNIT_SPAWNER, tileLocation = tile,
                    playerControlled = false).let{
                it.equipment.add(StandardEquipment.sword)
            }
        }
        val masterOrgans = tiledMapInterpreter.getMasterOrgan(boardProvider.tiledMap)
        for (tile in masterOrgans){
            entitySpawner.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.MASTER_ORGAN, tileLocation = tile,
                    playerControlled = false).let{
            }
        }
        val shieldingOrgans = tiledMapInterpreter.getShieldingOrgan(boardProvider.tiledMap)
        for (tile in shieldingOrgans){
            entitySpawner.addCharacterToTile(tacMapUnit = TacMapUnitTemplate.SHIELDING_ORGAN, tileLocation = tile,
                    playerControlled = false).let{
            }
        }

        for (char in tacmapState.listOfCharacters){
            val allCharacterFunctionalAttributes = functionalCharacterAttributeFactory.getFunctionalAttributesForCharacter(char)
            for (func in allCharacterFunctionalAttributes){
                func.onInitialization(char)
            }
        }

        animationActionQueueProvider.runThroughActionQueue()
    }
}