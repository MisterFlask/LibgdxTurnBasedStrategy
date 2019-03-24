package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttributeFactory
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.StandardEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.tiledutils.BoundingRectangle
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
                                            val logicHooks: LogicHooks,
                                            val animationActionQueueProvider: AnimationActionQueueProvider,
                                            val entitySpawner: EntitySpawner,
                                            val attributeOperator: AttributeOperator){
    fun startBattle(){
        println("Starting battle")
        val legitTiles = tiledMapInterpreter.getPossiblePlayerSpawnPositions(boardProvider.tiledMap)
        for (tile in legitTiles){
            entitySpawner.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.DEFAULT_UNIT.copy(

                    equipment = arrayListOf(StandardEquipment.sword, StandardEquipment.flamethrower)

            ), tileLocation = tile,
                    playerControlled = true)
        }
        val enemyTiles = tiledMapInterpreter.getPossibleEnemySpawnPositions(boardProvider.tiledMap)
        for (tile in enemyTiles){
            entitySpawner.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.DEFAULT_ENEMY_UNIT,
                    tileLocation = tile,
                    playerControlled = false)
        }
        val spawners = tiledMapInterpreter.getSpawners(boardProvider.tiledMap)
        for (tile in spawners){
            entitySpawner.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.DEFAULT_ENEMY_UNIT_SPAWNER, tileLocation = tile,
                    playerControlled = false)
        }
        val masterOrgans = tiledMapInterpreter.getMasterOrgan(boardProvider.tiledMap)
        for (tile in masterOrgans){
            entitySpawner.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.MASTER_ORGAN, tileLocation = tile,
                    playerControlled = false).let{
            }
        }
        val shieldingOrgans = tiledMapInterpreter.getShieldingOrgan(boardProvider.tiledMap)
        for (tile in shieldingOrgans){
            entitySpawner.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.SHIELDING_ORGAN, tileLocation = tile,
                    playerControlled = false).let{
            }
        }

        for (char in tacmapState.listOfCharacters){
            logicHooks.onUnitCreation(char)
        }
        for (char in tacmapState.listOfEnemyCharacters){
            attributeOperator.applyAttribute(char, LogicalCharacterAttribute.SNOOZING)
        }

        animationActionQueueProvider.runThroughActionQueue()
    }
}