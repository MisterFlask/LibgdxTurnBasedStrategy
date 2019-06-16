package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class EnemyAiFactory @Inject constructor(val tiledMapOperationsHandler: TiledMapOperationsHandler,
                                                val tileMapProvider: TileMapProvider,
                                                val aiGridGraphFactory: AiGridGraphFactory,
                                                val tacticalMapState: TacticalMapState,
                                                val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                val basicAiDecisions: BasicAiDecisions){

    public fun getEnemyAi(enemyAiType: EnemyAiType) : EnemyAi{
        when(enemyAiType){
            EnemyAiType.BASIC -> return BasicEnemyAi(
                    tiledMapOperationsHandler,
                    tacticalMapState,
                    tileMapProvider,
                    aiGridGraphFactory,
                    mapAlgorithms = tacticalMapAlgorithms,
                    basicAiDecisions = basicAiDecisions);
            EnemyAiType.IMMOBILE_UNIT -> return ImmobileEnemyAi()
        }
    }

}

