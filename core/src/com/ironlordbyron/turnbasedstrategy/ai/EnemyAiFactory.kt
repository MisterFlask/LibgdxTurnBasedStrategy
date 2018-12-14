package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TacticalTileMap
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class EnemyAiFactory @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                                                val tileMapProvider: TileMapProvider,
                                                val aiGridGraphFactory: AiGridGraphFactory,
                                                val tacticalMapState: TacticalMapState,
                                                val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                val abilityFactory: AbilityFactory){

    public fun getEnemyAi(enemyAiType: EnemyAiType) : EnemyAi{
        when(enemyAiType){
            EnemyAiType.BASIC -> return BasicEnemyAi(
                    tileMapOperationsHandler,
                    tacticalMapState,
                    tileMapProvider,
                    aiGridGraphFactory,
                    mapAlgorithms = tacticalMapAlgorithms,
                    abilityFactory = abilityFactory);
        }
    }

}

