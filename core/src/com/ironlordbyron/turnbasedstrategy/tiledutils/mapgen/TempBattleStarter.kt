package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.entrypoints.CadenceEffectsRegistrar
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.missiongen.TileZone
import com.ironlordbyron.turnbasedstrategy.missiongen.ZoneStyleMissionUnitTemplateDecider
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
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

    fun getTilesByKeyValuePairs(kvs: List<TileKeyValuePair>): List<TileLocation> {
        return tiledMap.getTilesByKeyValuePairs(kvs)
    }

    fun getSpawnableTilemapTiles(): List<TileLocation> {
        val ret = tiledMap.getSpawnableEnemyTilemapTiles()
        if (ret.isNotEmpty()) return ret
        return getDiscreteZones().flatMap { it.tiles }
    }
    fun getPlayerPlacementTilemapTiles() : List<TileLocation>{
        return tiledMap.getPlayerPlacementTiles()
    }
    fun getDiscreteZones() : List<TileZone>{
        return tiledMap.getObjectLayerRectangles(layerName = "Zones")
                .map{ TileZone(it.toLocations()) }
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
                                            val attributeActionManager: AttributeActionManager,
                                            val unitTemplateRegistrar: UnitTemplateRegistrar){
    val cadenceEffectsRegistrar: CadenceEffectsRegistrar by lazy {
        GameModuleInjector.generateInstance(CadenceEffectsRegistrar::class.java)
    }

    val zoneStyleMissionUnitTemplateDecider by LazyInject(ZoneStyleMissionUnitTemplateDecider::class.java)

    fun startBattle(){
        println("Starting battle")
        cadenceEffectsRegistrar.turnStartEffects.forEach{it.handleTurnStartEvent()}

        for (spawner in unitTemplateRegistrar.unitTemplates){
            val unitId = spawner.id
            val qualifyingTiles = tiledMapInterpreter.getSpawnedTacMapUnit(boardProvider.tiledMap, unitId)
            for (tile in qualifyingTiles){
                actionManager.addCharacterToTileFromTemplate(tacMapUnit = spawner.spawn(),
                        tileLocation = tile,
                        playerControlled = false)
            }
        }

        val templatesForZones = zoneStyleMissionUnitTemplateDecider.createUnitsAndOrganGenerationParameters()
        for (item in templatesForZones){
            val logicalCharacter = actionManager.addCharacterToTileFromTemplate(tacMapUnit = item.tacMapUnitTemplate,
                    tileLocation = item.tile,
                    playerControlled = false)
            for (attr in item.attrsToApply){
                attributeActionManager.applyAttribute(logicalCharacter, attr)
            }
        }

        for (char in tacmapState.listOfCharacters){
            logicHooks.onUnitCreation(char)
        }

        actionManager.createAwaitedSpeechBubbleForCharacter("And now, we begin.",
                TacMapUnitTemplate.DEFAULT_ENEMY_UNIT)

        animationActionQueueProvider.runThroughActionQueue()
    }
}