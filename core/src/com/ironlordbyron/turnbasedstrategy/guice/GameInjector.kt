package com.ironlordbyron.turnbasedstrategy.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TiledMapStageFactory
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.BlankMapGenerator
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.GameDataProvider

class GameModule : AbstractModule() {
    override fun configure() {
    }
}

class GameModuleInjector {
    companion object {
        private val moduleInjector = Guice.createInjector(GameModule())
        fun createTiledMapOperationsHandler(): TileMapOperationsHandler {
            return moduleInjector.getInstance(TileMapOperationsHandler::class.java);
        }

        fun createGameStateProvider(): GameDataProvider {
            return moduleInjector.getInstance(GameDataProvider::class.java)
        }
        fun createTiledMapGenerator(): BlankMapGenerator {
            return moduleInjector.getInstance(BlankMapGenerator::class.java)
        }

        fun createTiledMapStageFactory(): TiledMapStageFactory {
            return moduleInjector.getInstance(TiledMapStageFactory::class.java)
        }
    }
}