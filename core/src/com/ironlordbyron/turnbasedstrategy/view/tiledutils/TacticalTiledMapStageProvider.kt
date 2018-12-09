package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class TacticalTiledMapStageProvider : Provider<TiledMapStage> {
    override fun get(): TiledMapStage {
        return tiledMapStage
    }

    lateinit var tiledMapStage: TiledMapStage
}
