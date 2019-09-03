package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.MapGeneratorType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.MobGenerationParams
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams

public class Scenarios
{
    companion object {
        val DEFAULT_SCENARIO = ScenarioParams(sourceMapName = "BlankGrass.tmx",
                name = "Default Scenario", mapGeneratorType = MapGeneratorType.NONE,unitsThatPlayerWillDeploy = listOf())
        val PARTIAL_PROCEDURAL_SCENARIO = ScenarioParams(sourceMapName = "LargerBlankGrass.tmx",
                name = "Partially Procedural", mapGeneratorType = MapGeneratorType.PARTIAL_PROCEDURAL,
                mobGenerationParams = MobGenerationParams(numberMobsToGenerate = 10, totalDifficultyAllowed = 10),
                unitsThatPlayerWillDeploy = listOf())
        val OVERWORLD_SCENARIO = ScenarioParams(sourceMapName = "OutdoorCombatDemo.tmx",
                name = "Overworld Combat", mapGeneratorType = MapGeneratorType.NONE,
                unitsThatPlayerWillDeploy = listOf())
        val EREBUS_SCENARIO = ScenarioParams(sourceMapName = "tileMaps/erebusstyle.tmx",
                name = "Erebus-style Overworld Combat", mapGeneratorType = MapGeneratorType.NONE,
                unitsThatPlayerWillDeploy = listOf())
        val SMALLER_EREBUS_SCENARIO = ScenarioParams(sourceMapName = "tileMaps/SmallerErebusStyle.tmx",
                name = "Smaller Erebus-style Overworld Combat", mapGeneratorType = MapGeneratorType.NONE,
                unitsThatPlayerWillDeploy = listOf())
        val INDOOR_TEST_2 = ScenarioParams(sourceMapName = "tileMaps/IndoorMapTest.tmx",
                name = "Indoor Test Combat",
                mapGeneratorType = MapGeneratorType.NONE,
                unitsThatPlayerWillDeploy = listOf())


        val SCENARIO_LIST = listOf(DEFAULT_SCENARIO, PARTIAL_PROCEDURAL_SCENARIO, OVERWORLD_SCENARIO, EREBUS_SCENARIO,
                SMALLER_EREBUS_SCENARIO, INDOOR_TEST_2)
    }
}