package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.ironlordbyron.turnbasedstrategy.view.ui.TacMapHud
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
class StageProvider : Provider<TiledMapStage> {
    override fun get(): TiledMapStage {
        return tiledMapStage
    }

    lateinit var tacMapHudStage: TacMapHud
    lateinit var tiledMapStage: TiledMapStage
}
