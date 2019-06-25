package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.MapGeneratorType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.MobGenerationParams
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams

public class Scenarios
{
    companion object {
        val DEFAULT_SCENARIO = ScenarioParams(sourceMapName = "BlankGrass.tmx",
                name = "Default Scenario", mapGeneratorType = MapGeneratorType.NONE)
        val PARTIAL_PROCEDURAL_SCENARIO = ScenarioParams(sourceMapName = "LargerBlankGrass.tmx",
                name = "Partially Procedural", mapGeneratorType = MapGeneratorType.PARTIAL_PROCEDURAL,
                mobGenerationParams = MobGenerationParams(numberMobsToGenerate = 10, totalDifficultyAllowed = 10))
        val OVERWORLD_SCENARIO = ScenarioParams(sourceMapName = "OutdoorCombatDemo.tmx",
                name = "Overworld Combat", mapGeneratorType = MapGeneratorType.NONE)
        val SCENARIO_LIST = listOf(DEFAULT_SCENARIO, PARTIAL_PROCEDURAL_SCENARIO, OVERWORLD_SCENARIO)
    }
}