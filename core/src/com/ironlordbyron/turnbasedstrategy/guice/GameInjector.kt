package com.ironlordbyron.turnbasedstrategy.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provider
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.BlankMapGenerator
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider

class GameModule : AbstractModule() {
    override fun configure() {
    }
}

class GameModuleInjector {
    companion object {
        private val moduleInjector = Guice.createInjector(GameModule())
        fun createTiledMapOperationsHandler(): TileMapOperationsHandler {
            return moduleInjector.getInstance(TileMapOperationsHandler::class.java)
        }

        fun createSpriteActorFactory() : CharacterActorFactory {
            return moduleInjector.getInstance(CharacterActorFactory::class.java)
        }

        fun createGameStateProvider(): TileMapProvider {
            return moduleInjector.getInstance(TileMapProvider::class.java)
        }
        fun createTiledMapGenerator(): BlankMapGenerator {
            return moduleInjector.getInstance(BlankMapGenerator::class.java)
        }

        fun createTiledMapStageFactory(): TiledMapStageFactory {
            return moduleInjector.getInstance(TiledMapStageFactory::class.java)
        }
    }
}