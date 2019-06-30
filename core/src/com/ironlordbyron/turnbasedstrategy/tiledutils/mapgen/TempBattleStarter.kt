package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.specific.SNOOZING
import com.ironlordbyron.turnbasedstrategy.common.equipment.StandardEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.tacmapunits.ShieldingOrgan
import com.ironlordbyron.turnbasedstrategy.tacmapunits.WeakMinionSpawner
import com.ironlordbyron.turnbasedstrategy.tiledutils.BoundingRectangle
import com.ironlordbyron.turnbasedstrategy.tiledutils.TileLayer
import com.ironlordbyron.turnbasedstrategy.tiledutils.getBoundsOfTile
import com.ironlordbyron.turnbasedstrategy.tiledutils.getTileLayer
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import javax.inject.Inject
import javax.inject.Singleton

public enum class BoundingBoxType{
    WHOLE_TILE,
    UPPER_TILE
}

@Singleton
class TileMapProvider {
    lateinit var tiledMap: TiledMap

    fun getWidth(): Int{
        return tiledMap.getTileLayer(TileLayer.BASE).width
    }
    fun getHeight():Int{
        return tiledMap.getTileLayer(TileLayer.BASE).height
    }

    fun getBoundingBoxOfTile(tileLocation: TileLocation, boundingBoxType: BoundingBoxType = BoundingBoxType.WHOLE_TILE): BoundingRectangle {
        val boundingRectangle = (tiledMap.layers[0] as TiledMapTileLayer).getBoundsOfTile(tileLocation)

        when(boundingBoxType){
            BoundingBoxType.WHOLE_TILE -> return boundingRectangle
            BoundingBoxType.UPPER_TILE -> {
                val newBoundingRectangle = BoundingRectangle(boundingRectangle.x,
                        boundingRectangle.y - boundingRectangle.height / 2,
                        boundingRectangle.width / 2,
                        boundingRectangle.height / 2)
                return newBoundingRectangle
            }
        }
    }

}


@Singleton
class TempBattleStarter @Inject constructor(val boardProvider: TileMapProvider,
                                            val gameBoardOperator: GameBoardOperator,
                                            val tiledMapInterpreter: TiledMapInterpreter,
                                            val tacmapState: TacticalMapState,
                                            val logicHooks: LogicHooks,
                                            val animationActionQueueProvider: AnimationActionQueueProvider,
                                            val actionManager: ActionManager,
                                            val attributeOperator: AttributeOperator,
                                            val unitTemplateRegistrar: UnitTemplateRegistrar){
    fun startBattle(){
        println("Starting battle")
        val playerSpawns = tiledMapInterpreter.getPossiblePlayerSpawnPositions(boardProvider.tiledMap)
        for (tile in playerSpawns){
            actionManager.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.DEFAULT_UNIT, tileLocation = tile,
                    playerControlled = true)
        }
        val enemyTiles = tiledMapInterpreter.getPossibleEnemySpawnPositions(boardProvider.tiledMap)
        for (tile in enemyTiles){
            actionManager.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.DEFAULT_ENEMY_UNIT,
                    tileLocation = tile,
                    playerControlled = false)
        }
        val spawners = tiledMapInterpreter.getSpawners(boardProvider.tiledMap)
        for (tile in spawners){
            actionManager.addCharacterToTileFromTemplate(tacMapUnit = WeakMinionSpawner(), tileLocation = tile,
                    playerControlled = false)
        }
        val masterOrgans = tiledMapInterpreter.getMasterOrgan(boardProvider.tiledMap)
        for (tile in masterOrgans){
            actionManager.addCharacterToTileFromTemplate(tacMapUnit = TacMapUnitTemplate.MASTER_ORGAN, tileLocation = tile,
                    playerControlled = false)
        }
        val shieldingOrgans = tiledMapInterpreter.getShieldingOrgan(boardProvider.tiledMap)
        for (tile in shieldingOrgans){
            actionManager.addCharacterToTileFromTemplate(tacMapUnit = ShieldingOrgan(),
                    tileLocation = tile,
                    playerControlled = false)
        }

        for (spawner in unitTemplateRegistrar.unitTemplates){
            val unitId = spawner.id
            val qualifyingTiles = tiledMapInterpreter.getSpawnedTacMapUnit(boardProvider.tiledMap, unitId)
            for (tile in qualifyingTiles){
                actionManager.addCharacterToTileFromTemplate(tacMapUnit = spawner.spawn(),
                        tileLocation = tile,
                        playerControlled = false)
            }
        }

        for (char in tacmapState.listOfCharacters){
            logicHooks.onUnitCreation(char)
        }
        for (char in tacmapState.listOfEnemyCharacters){
            attributeOperator.applyAttribute(char, SNOOZING)
        }

        animationActionQueueProvider.runThroughActionQueue()
    }
}