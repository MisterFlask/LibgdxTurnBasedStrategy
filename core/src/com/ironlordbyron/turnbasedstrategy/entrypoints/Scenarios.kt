package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.MapGeneratorType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams

public class Scenarios
{
    companion object {
        val DEFAULT_SCENARIO = ScenarioParams(sourceMapName = "BlankGrass.tmx",
                name = "Default Scenario", mapGeneratorType = MapGeneratorType.NONE)
        val SCENARIO_LIST = listOf(DEFAULT_SCENARIO)
    }
}