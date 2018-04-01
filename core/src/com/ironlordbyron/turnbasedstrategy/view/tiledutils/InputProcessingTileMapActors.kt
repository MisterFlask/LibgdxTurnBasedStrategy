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

data class LogicalTile(val terrainTile: TiledMapTile, val location: TileLocation, val actor: TileMapActor,
                       val cell: TiledMapTileLayer.Cell, val allTilesAtThisSquare: List<TiledMapStage.TiledCellAgglomerate>) {
    fun isTerrainMountainous(): Boolean {
        return layerHasBooleanPropertySetToTrue(TileLayer.FEATURE, "mountain")
    }

    fun layerHasBooleanPropertySetToTrue(layer: TileLayer, property: String): Boolean {
        val prop = allTilesAtThisSquare
                .firstOrNull { it.tileLayer == layer }
                ?.tiledCell?.tile?.properties?.get(property)
        if (prop == null) {
            return false
        }
        if (prop is Boolean) {
            return prop
        } else {
            throw IllegalStateException("Property $property should be a boolean, but it's a ${prop.javaClass.name}")
        }

    }
}

data class LibgdxLocation(val x: Int, val y: Int)

@Singleton
class LogicalTileTracker {
    val tiles = ArrayList<LogicalTile>()

    fun addTile(logicalTile: LogicalTile) {
        tiles.add(logicalTile)
    }

    fun getLogicalTileFromTile(tile: TiledMapTile): LogicalTile {
        return tiles.first { it.terrainTile === tile }
    }

    fun getLogicalTileFromLocation(loc: TileLocation): LogicalTile? {
        return tiles.first { it.location == loc }
    }

    fun getLibgdxCoordinatesFromLocation(loc: TileLocation): LibgdxLocation {
        val tileActor = tiles.first { it.location == loc }.actor

        return LibgdxLocation(tileActor.x.toInt(), tileActor.y.toInt()) // TODO: Verify
    }
}

@Singleton
class TacticalTiledMapStageProvider : Provider<TiledMapStage> {
    override fun get(): TiledMapStage {
        return tiledMapStage
    }

    lateinit var tiledMapStage: TiledMapStage
}

@Singleton
class TiledMapStage(@Assisted val tiledMap: TiledMap,
                    val tileMapClickListenerActorFactory: TileMapClickListenerActorFactory,
                    val tileMapClickListenerFactory: TileMapClickListenerFactory,
                    @Assisted val orthographicCamera: OrthographicCamera,
                    val logicalTileTracker: LogicalTileTracker,
                    val battleStarter: TempBattleStarter,
                    val spriteActorFactory: SpriteActorFactory,
                    val tileMapProvider: TileMapProvider,
                    val tacticalTiledMapStageProvider: TacticalTiledMapStageProvider) : Stage(), InputProcessor {
    init {
        tacticalTiledMapStageProvider.tiledMapStage = this
        val layer = tiledMap.getTileLayer(TileLayer.BASE)
        createActorsAndLocationsForLayer(layer, tiledMap)
        createFactoriesForStage()
        battleStarter.startBattle()
    }

    private fun createFactoriesForStage() {
        tileMapProvider.tiledMap = tiledMap
    }

    private fun getAllTilesAtXY(tileMap: TiledMap, tileLocation: TileLocation): List<TiledCellAgglomerate> {
        val layers = tileMap.layers
                .filter { it is TiledMapTileLayer }
                .map { it as TiledMapTileLayer }
                .filter { it.getCell(tileLocation.x, tileLocation.y) != null }
                .filter { TileLayer.getTileLayerFromName(it.name) != null }
                .map { TiledCellAgglomerate(it.getCell(tileLocation.x, tileLocation.y), TileLayer.getTileLayerFromName(it.name)!!) }
        return layers
    }

    data class TiledCellAgglomerate(val tiledCell: TiledMapTileLayer.Cell, val tileLayer: TileLayer)

    private fun createActorsAndLocationsForLayer(tiledLayer: TiledMapTileLayer, tiledMap: TiledMap) {

        for (x in 0..tiledLayer.width) {
            for (y in 0..tiledLayer.height) {
                val cell = tiledLayer.getCell(x, y) ?: continue
                println("Assigning actor to cell ID ${cell.tile.id} at $x $y}")
                val actor = tileMapClickListenerActorFactory.createTileMapActor(this.tiledMap, tiledLayer, cell, TileLocation(x, y)
                )
                actor.setBounds(x * tiledLayer.tileWidth, y * tiledLayer.tileHeight, tiledLayer.tileWidth,
                        tiledLayer.tileHeight)
                logicalTileTracker.addTile(LogicalTile(cell.tile, TileLocation(x, y), actor, cell,
                        getAllTilesAtXY(tiledMap, TileLocation(x, y))))
                addActor(actor)
                val eventListener = tileMapClickListenerFactory.create(actor)
                actor.addListener(eventListener)
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