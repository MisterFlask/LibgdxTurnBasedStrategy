package com.ironlordbyron.turnbasedstrategy.guice

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.FragmentCopier
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TiledMapStageFactory

class GameModule : AbstractModule() {
    override fun configure() {
    }
}

class GameModuleInjector {
    companion object {
        private val moduleInjector = Guice.createInjector(GameModule())
        fun createFragmentCopier(): FragmentCopier {
            return moduleInjector.getInstance(FragmentCopier::class.java);
        }

        fun createTiledMapStageFactory(): TiledMapStageFactory {
            return moduleInjector.getInstance(TiledMapStageFactory::class.java)
        }
    }
}