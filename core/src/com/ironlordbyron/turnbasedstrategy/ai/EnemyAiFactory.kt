package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class EnemyAiFactory @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                                                val tileMapProvider: TileMapProvider,
                                                val aiGridGraphFactory: AiGridGraphFactory){

    public fun getEnemyAi(enemyAiType: EnemyAiType) : EnemyAi{
        when(enemyAiType){
            EnemyAiType.BASIC -> return BasicEnemyAi(tileMapOperationsHandler, tileMapProvider, aiGridGraphFactory);
        }
    }

}

