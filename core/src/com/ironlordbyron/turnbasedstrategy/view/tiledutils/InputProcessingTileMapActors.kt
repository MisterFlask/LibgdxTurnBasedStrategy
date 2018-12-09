package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.google.inject.assistedinject.Assisted
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.controller.TacticalMapController
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TempBattleStarter
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

class TileMapActor(@Assisted val tiledMap: TiledMap,
                   @Assisted val tiledMapTileLayer: TiledMapTileLayer,
                   @Assisted val cell: TiledMapTileLayer.Cell,
                   @Assisted val location: TileLocation,
                   val fragmentCopier: TileMapOperationsHandler) : Actor()


@Singleton
class TileMapClickListenerFactory @Inject constructor(val eventNotifier: EventNotifier) {
    fun create(actor: TileMapActor): TileMapClickListener {
        return TileMapClickListener(actor, eventNotifier)
    }
}

/**
 * Defines action to be performed when user clicks on a tile.
 */
class TileMapClickListener(@Assisted val actor: TileMapActor,
                           val eventNotifier: EventNotifier) : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float,
                           y: Float, pointer: Int, button: Int): Boolean {
        println("X: ${actor.location.x}, y: ${actor.location.y}")
        eventNotifier.notifyListeners(TacticalGuiEvent.TileClicked(actor.location))
        return false
    }
}

/**
 * Used internally by Guice.
 */
@Singleton
class TiledMapStageFactory @Inject constructor(val tileMapClickListenerActorFactory: Provider<TileMapClickListenerActorFactory>,
                                               val tileMapClickListenerFactoryProvider: Provider<TileMapClickListenerFactory>,
                                               val logicalTileTracker: LogicalTileTracker,
                                               val characterPuller: CharacterImageManager,
                                               val tileMapOperationsHandler: TileMapOperationsHandler,
                                               val battleStarter: TempBattleStarter,
                                               val spriteActorFactory: SpriteActorFactory,
                                               val tileMapProvider: TileMapProvider,
                                               val tacticalMapController: TacticalMapController,
                                               val tacticalTiledMapStageProvider: TacticalTiledMapStageProvider) {
    fun create(tiledMap: TiledMap, orthographicCamera: OrthographicCamera): TiledMapStage {
        return TiledMapStage(tiledMap, tileMapClickListenerActorFactory.get(), tileMapClickListenerFactoryProvider.get(),
                orthographicCamera, logicalTileTracker, battleStarter, spriteActorFactory,
                tileMapProvider, tacticalTiledMapStageProvider)
    }
}

@Singleton
class TileMapClickListenerActorFactory @Inject constructor(val fragmentCopierProvider: Provider<TileMapOperationsHandler>) {
    fun createTileMapActor(tiledMap: TiledMap, tiledLayer: TiledMapTileLayer, cell: TiledMapTileLayer.Cell,
                           tileLocation: TileLocation): TileMapActor {
        return TileMapActor(tiledMap, tiledLayer, cell, tileLocation,
                fragmentCopierProvider.get())
    }
}


data class LibgdxLocation(val x: Int, val y: Int)


