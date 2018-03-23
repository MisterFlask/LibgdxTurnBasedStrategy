package com.ironlordbyron.turnbasedstrategy.view.tiledutils

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
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.BattleStarter
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

data class TileLocation(val x: Int, val y: Int)

class TileMapActor(@Assisted val tiledMap: TiledMap,
                   @Assisted val tiledMapTileLayer: TiledMapTileLayer,
                   @Assisted val cell: TiledMapTileLayer.Cell,
                   @Assisted val location: TileLocation,
                   val fragmentCopier: TileMapOperationsHandler) : Actor() {
};


@Singleton
public class TileMapClickListenerFactory @Inject constructor(val fragmentCopierProvider: Provider<TileMapOperationsHandler>) {
    public fun create(actor: TileMapActor): TileMapClickListener {
        return TileMapClickListener(actor, fragmentCopierProvider.get())
    }
}

/**
 * Defines action to be performed when user clicks on a tile.
 */
class TileMapClickListener(@Assisted val actor: TileMapActor,
                           val fragmentCopier: TileMapOperationsHandler) : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float,
                           y: Float, pointer: Int, button: Int): Boolean {
        println("X: ${actor.location.x}, y: ${actor.location.y}")
        fragmentCopier.copyFragmentTo("BlankGrass.tmx",
                minX = actor.location.x,
                minY = actor.location.y,
                fragmentName = TileMapFragment.City)
        return false;
    }
}

/**
 * Used internally by Guice.
 */
@Singleton
public class TiledMapStageFactory @Inject constructor(val actorFactory: Provider<ActorFactory>,
                                                      val tileMapClickListenerFactoryProvider: Provider<TileMapClickListenerFactory>,
                                                      val logicalTileTracker: LogicalTileTracker,
                                                      val characterPuller: CharacterImageManager,
                                                      val tileMapOperationsHandler: TileMapOperationsHandler,
                                                      val battleStarter: BattleStarter) {
    public fun create(tiledMap: TiledMap, orthographicCamera: OrthographicCamera): TiledMapStage {
        return TiledMapStage(tiledMap, actorFactory.get(), tileMapClickListenerFactoryProvider.get(),
                orthographicCamera, logicalTileTracker, characterPuller, tileMapOperationsHandler, battleStarter)
    }
}

@Singleton
public class ActorFactory @Inject constructor(val fragmentCopierProvider: Provider<TileMapOperationsHandler>) {
    public fun create(tiledMap: TiledMap, tiledLayer: TiledMapTileLayer, cell: TiledMapTileLayer.Cell,
                      tileLocation: TileLocation): TileMapActor {
        return TileMapActor(tiledMap, tiledLayer, cell, tileLocation,
                fragmentCopierProvider.get());
    }
}

public data class LogicalTile(val tiledTile: TiledMapTile, val location: TileLocation, val actor: TileMapActor,
                              val cell: TiledMapTileLayer.Cell)

@Singleton
public class LogicalTileTracker{
    val tiles = ArrayList<LogicalTile>()

    fun addTile(logicalTile: LogicalTile){
        tiles.add(logicalTile)
    }

    fun getLogicalTileFromTile(tile: TiledMapTile): LogicalTile {
        return tiles.first{it.tiledTile === tile}
    }
}
@Singleton
class TiledMapStage(@Assisted val tiledMap: TiledMap,
                    val actorFactory: ActorFactory,
                    val tileMapClickListenerFactory: TileMapClickListenerFactory,
                    @Assisted val orthographicCamera: OrthographicCamera,
                    val logicalTileTracker: LogicalTileTracker,
                    val characterPuller: CharacterImageManager,
                    val tileMapOperationsHandler: TileMapOperationsHandler,
                    val battleStarter: BattleStarter) : Stage(), InputProcessor {
    init {
        val layer = tiledMap.getTileLayer(TileLayer.BASE)
        createActorsAndLocationsForLayer(layer);
        // TODO: Move out of init function
        battleStarter.startBattle()
    }

    private fun createActorsAndLocationsForLayer(tiledLayer: TiledMapTileLayer) {
        for (x in 0..tiledLayer.width) {
            for (y in 0..tiledLayer.height) {
                val cell = tiledLayer.getCell(x, y) ?: continue;
                println("Assigning actor to cell ID ${cell.tile.id} at $x $y}")
                val actor = actorFactory.create(tiledMap, tiledLayer, cell, TileLocation(x, y)
                );
                actor.setBounds(x * tiledLayer.tileWidth, y * tiledLayer.tileHeight, tiledLayer.tileWidth,
                        tiledLayer.tileHeight);
                logicalTileTracker.addTile(LogicalTile(cell.tile, TileLocation(x,y), actor, cell))
                addActor(actor)
                val eventListener = tileMapClickListenerFactory.create(actor);
                actor.addListener(eventListener);
            }
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.LEFT)
            orthographicCamera.translate(-32f, 0f)
        if (keycode == Input.Keys.RIGHT)
            orthographicCamera.translate(32f, 0f)
        if (keycode == Input.Keys.UP)
            orthographicCamera.translate(0f, -32f)
        if (keycode == Input.Keys.DOWN)
            orthographicCamera.translate(0f, 32f)
        if (keycode == Input.Keys.NUM_1)
            tiledMap.layers.get(0).isVisible = !tiledMap.layers.get(0).isVisible
        if (keycode == Input.Keys.NUM_2)
            tiledMap.layers.get(1).isVisible = !tiledMap.layers.get(1).isVisible
        return false
    }

}