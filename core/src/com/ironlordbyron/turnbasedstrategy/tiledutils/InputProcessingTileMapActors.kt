package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.google.inject.assistedinject.Assisted
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.controller.TacticalMapController
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TempBattleStarter
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

class TileMapActor(@Assisted val tiledMap: TiledMap,
                   @Assisted val tiledMapTileLayer: TiledMapTileLayer,
                   @Assisted val cell: TiledMapTileLayer.Cell,
                   @Assisted val location: TileLocation,
                   val fragmentCopier: TiledMapOperationsHandler) : Actor()


@Singleton
class TileMapClickListenerFactory @Inject constructor(val eventNotifier: EventNotifier) {
    fun create(actor: TileMapActor): TileMapClickListener {
        return TileMapClickListener(actor, eventNotifier)
    }
}

/**
 * Defines action to be performed when user clicks on or hovers over a tile.
 */
class TileMapClickListener(@Assisted val actor: TileMapActor,
                           val eventNotifier: EventNotifier) : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float,
                           y: Float, pointer: Int, button: Int): Boolean {
        eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.TileClicked(actor.location))
        return false
    }

    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.TileHovered(actor.location))
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
                                               val tiledMapOperationsHandler: TiledMapOperationsHandler,
                                               val battleStarter: TempBattleStarter,
                                               val spriteActorFactory: SpriteActorFactory,
                                               val tileMapProvider: TileMapProvider,
                                               val tacticalMapController: TacticalMapController,
                                               val tacticalTiledMapStageProvider: TacticalTiledMapStageProvider,
                                               val tiledMapInterpreter: TiledMapInterpreter) {
    fun create(tiledMap: TiledMap, orthographicCamera: OrthographicCamera): TiledMapStage {
        return TiledMapStage(tiledMap, tileMapClickListenerActorFactory.get(), tileMapClickListenerFactoryProvider.get(),
                orthographicCamera, logicalTileTracker, battleStarter, spriteActorFactory,
                tileMapProvider, tacticalTiledMapStageProvider, tiledMapInterpreter)
    }
}

@Singleton
class TileMapClickListenerActorFactory @Inject constructor(val fragmentCopierProvider: Provider<TiledMapOperationsHandler>) {
    fun createTileMapActor(tiledMap: TiledMap, tiledLayer: TiledMapTileLayer, cell: TiledMapTileLayer.Cell,
                           tileLocation: TileLocation): TileMapActor {
        return TileMapActor(tiledMap, tiledLayer, cell, tileLocation,
                fragmentCopierProvider.get())
    }
}


data class LibgdxLocation(val x: Int, val y: Int)


