package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import com.ironlordbyron.turnbasedstrategy.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
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
            EnemyAiType.IMMOBILE_UNIT -> return ImmobileEnemyAi(
                    abilityFactory
            )
        }
    }

}

