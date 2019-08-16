package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import java.util.*
import javax.inject.Singleton

/**
 * Responsible for initializing a tilemap from a scenario.
 */
@Singleton
class BlankMapGenerator @Inject constructor(val fragmentCopier: TiledMapOperationsHandler,
                                            val tileTracker: LogicalTileTracker) {

    fun generateMap(params: ScenarioParams): TiledMap {
        val blankMap = fragmentCopier.getTileMap(params.sourceMapName, MapType.SOURCE_MAP, allowCaching = false)
        /**
        val blankMapFirstLayer = blankMap.getTileLayer(TileLayer.BASE)
        val locationsUsed: HashSet<TileLocation> = hashSetOf()
        for (i in 0..params.numCities) {
            val cityTileMap = fragmentCopier.pullTileMapLayer(params.cityFragmentMapName, MapType.FRAGMENT_MAP, tileLayer = TileLayer.BASE)
            val width = cityTileMap.width
            val height = cityTileMap.height
            val randomX = Random().nextInt(blankMapFirstLayer.width - width)
            val randomY = Random().nextInt(blankMapFirstLayer.height - height)

            // fitting function
            val fits = doesItFit(width, height, locationsUsed, randomX, randomY)
            if (fits) {
                locationsUsed.addAll(getSpacesUsed(width, height, randomX, randomY))
                fragmentCopier.copyFragmentTo(params.sourceMapName, randomX, randomY, params.cityFragmentMapName)
                // TODO: Add functionality to enrich our map data with the fact that it's a city.
            }
        }
        */
        return blankMap
    }


    private fun getSpacesUsed(width: Int, height: Int, startX: Int, startY: Int): Set<TileLocation> {
        val used = HashSet<TileLocation>()
        for (x in startX..startX + width) {
            for (y in startY..startY + height) {
                used.add(TileLocation(x, y))
            }
        }
        return used
    }

    private fun doesItFit(width: Int, height: Int, locationsUsed: HashSet<TileLocation>, startX: Int, startY: Int): Boolean {
        var doesFit = true
        for (x in startX..startX + width) {
            for (y in startY..startY + height) {
                if (locationsUsed.contains(TileLocation(x, y))) {
                    doesFit = false
                }
            }
        }
        return doesFit
    }
}

data class MobGenerationParams(val numberMobsToGenerate: Int,
                               val totalDifficultyAllowed: Int)

data class ScenarioParams(val sourceMapName: String,
                          val name: String,
                          val mapGeneratorType: MapGeneratorType,
                          val mobGenerationParams: MobGenerationParams? = null,
                          val unitsThatPlayerWillDeploy: Collection<TacMapUnitTemplate>)
enum class MapGeneratorType{
    NONE,
    PARTIAL_PROCEDURAL,
    OUTDOORS_LARGE
}

/**
 * This runs the tiled map interpreter, and the chosen tiled map stage algorithm.
 */
public class MapGenerationApplicator @Inject constructor(val tiledMapInterpreter: TiledMapInterpreter,
                                                         val partiallyProceduralMapGenerator: PartiallyProceduralDungeonGenerator,
                                                         val tileMapProvider: TileMapProvider,
                                                         val outdoorMapGenerator: OutdoorsMapGenerator){
    fun generateMapForScenario(scenarioParams: ScenarioParams){
        when(scenarioParams.mapGeneratorType){
             MapGeneratorType.NONE -> {

            }
            MapGeneratorType.PARTIAL_PROCEDURAL -> {
                partiallyProceduralMapGenerator.generateDungeon(scenarioParams)
            }
            MapGeneratorType.OUTDOORS_LARGE -> {
                outdoorMapGenerator.generateMap(scenarioParams)
            }
        }
    }
}

class OutdoorsMapGenerator @Inject constructor(val tiledMapInterpreter: TiledMapInterpreter,
                                               val tileMapProvider: TileMapProvider) {
    fun generateMap(scenarioParams: ScenarioParams){

        for (i in 0 .. 10){

        }
    }
}

