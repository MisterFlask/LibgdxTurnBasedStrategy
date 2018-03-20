package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.google.inject.assistedinject.Assisted
import javax.inject.Inject
import javax.inject.Provider

data class TileLocation(val x: Int, val y: Int)

class TileMapActor(@Assisted val tiledMap: TiledMap,
                   @Assisted val tiledMapTileLayer: TiledMapTileLayer,
                   @Assisted val cell: TiledMapTileLayer.Cell,
                   @Assisted val location: TileLocation,
                   val fragmentCopier: FragmentCopier) : Actor() {
};


public class TileMapClickListenerFactory @Inject constructor(val fragmentCopierProvider: Provider<FragmentCopier>) {
    public fun create(actor: TileMapActor): TileMapClickListener {
        return TileMapClickListener(actor, fragmentCopierProvider.get())
    }
}

/**
 * Defines action to be performed when user clicks on a tile.
 */
class TileMapClickListener(@Assisted val actor: TileMapActor,
                           val fragmentCopier: FragmentCopier) : ClickListener() {
    override fun touchDown(event: InputEvent, x: Float,
                           y: Float, pointer: Int, button: Int): Boolean {
        println("X: ${actor.location.x}, y: ${actor.location.y}")
        fragmentCopier.copyFragmentTo(actor.tiledMap.GetTileLayer(TileLayer.FEATURE),
                minX = actor.location.x,
                minY = actor.location.y,
                fragmentName = TileMapFragment.City)
        return false;
    }
}

/**
 * Used internally by Guice.
 */
public class TiledMapStageFactory @Inject constructor(val actorFactory: Provider<ActorFactory>,
                                                      val tileMapClickListenerFactoryProvider: Provider<TileMapClickListenerFactory>) {
    public fun create(tiledMap: TiledMap, orthographicCamera: OrthographicCamera): TiledMapStage {
        return TiledMapStage(tiledMap, actorFactory.get(), tileMapClickListenerFactoryProvider.get(),
                orthographicCamera)
    }
}

public class ActorFactory @Inject constructor(val fragmentCopierProvider: Provider<FragmentCopier>) {
    public fun create(tiledMap: TiledMap, tiledLayer: TiledMapTileLayer, cell: TiledMapTileLayer.Cell,
                      tileLocation: TileLocation): TileMapActor {
        return TileMapActor(tiledMap, tiledLayer, cell, tileLocation,
                fragmentCopierProvider.get());
    }
}

class TiledMapStage(@Assisted val tiledMap: TiledMap,
                    val actorFactory: ActorFactory,
                    val tileMapClickListenerFactory: TileMapClickListenerFactory,
                    @Assisted val orthographicCamera: OrthographicCamera) : Stage(), InputProcessor {
    init {
        val layer = tiledMap.GetTileLayer(TileLayer.TERRAIN)
        createActorsForLayer(layer);
    }

    private fun createActorsForLayer(tiledLayer: TiledMapTileLayer) {
        for (x in 0..tiledLayer.width) {
            for (y in 0..tiledLayer.height) {
                val cell = tiledLayer.getCell(x, y) ?: continue;
                println("Assigning actor to cell ID ${cell.tile.id} at $x $y}")
                val actor = actorFactory.create(tiledMap, tiledLayer, cell, TileLocation(x, y)
                );
                actor.setBounds(x * tiledLayer.tileWidth, y * tiledLayer.tileHeight, tiledLayer.tileWidth,
                        tiledLayer.tileHeight);
                addActor(actor);
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