package com.ironlordbyron.turnbasedstrategy.guice

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.view.animation.TargetingCursorManager
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.BlankMapGenerator
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.GameCameraProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import com.ironlordbyron.turnbasedstrategy.view.animation.passive.EagerInitializer
import com.ironlordbyron.turnbasedstrategy.view.ui.TacMapHudFactory

class GameModule : AbstractModule() {
    override fun configure() {
    }
}

class GameModuleInjector {
    companion object {
        public val moduleInjector = Guice.createInjector(GameModule())

        init{
            moduleInjector.getInstance(TargetingCursorManager::class.java)
            moduleInjector.getInstance(EagerInitializer::class.java)
        }


        fun createTiledMapOperationsHandler(): TiledMapOperationsHandler {
            return moduleInjector.getInstance(TiledMapOperationsHandler::class.java)
        }

        fun createSpriteActorFactory() : SpriteActorFactory {
            return moduleInjector.getInstance(SpriteActorFactory::class.java)
        }

        fun createTacMapHudFactory(): TacMapHudFactory {
            return moduleInjector.getInstance(TacMapHudFactory::class.java)
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
        fun initGameCameraProvider(camera: Camera){
            var provider = moduleInjector.getInstance(GameCameraProvider::class.java)
            provider.camera = camera as OrthographicCamera
        }

        fun getGameCameraProvider() : GameCameraProvider {
            var provider = moduleInjector.getInstance(GameCameraProvider::class.java)
            return provider
        }

        fun getSpecialEffectManager() : SpecialEffectManager {
            var manager = moduleInjector.getInstance(SpecialEffectManager::class.java)
            return manager
        }

        fun getEventNotifier(): EventNotifier {
            return moduleInjector.getInstance(EventNotifier::class.java)
        }
    }
}