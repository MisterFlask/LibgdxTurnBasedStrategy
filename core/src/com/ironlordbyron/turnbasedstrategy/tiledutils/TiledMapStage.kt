package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.google.inject.assistedinject.Assisted
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TempBattleStarter
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import com.ironlordbyron.turnbasedstrategy.view.ActorName
import com.ironlordbyron.turnbasedstrategy.view.ActorOrdering
import com.ironlordbyron.turnbasedstrategy.view.ActorSortOrderComparator
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ImageIcon
import com.ironlordbyron.turnbasedstrategy.view.setFunctionalName
import javax.inject.Singleton


@Singleton
class TiledMapStage(
        val tileMapClickListenerActorFactory: TileMapClickListenerActorFactory,
        val tileMapClickListenerFactory: TileMapClickListenerFactory,
        @Assisted val orthographicCamera: OrthographicCamera,
        val logicalTileTracker: LogicalTileTracker,
        val battleStarter: TempBattleStarter,
        val spriteActorFactory: SpriteActorFactory,
        val tileMapProvider: TileMapProvider,
        val stageProvider: StageProvider,
        val tiledMapInterpreter: TiledMapInterpreter,
        val tiledMapProvider: TileMapProvider,
        val eventNotifier: EventNotifier,
        val tiledMapInitializer: TiledMapInitializer) : Stage(), InputProcessor {
    init {
        stageProvider.tiledMapStage = this
    }

    public fun reorderActors(){
        this.actors.sort(ActorSortOrderComparator())
    }

    public fun initializeBattle(tiledMap: TiledMap){
        stageProvider.tiledMapStage = this
        val layer = tiledMap.getTileLayer(TileLayer.BASE)
        createActorsAndLocationsForLayer(layer, tiledMap)
        createFactoriesForStage()
        tiledMapInitializer.initializeFromTilemap(tiledMap)
        battleStarter.startBattle()
    }

    private fun createFactoriesForStage() {
        tileMapProvider.tiledMap = tiledMapProvider.tiledMap
    }


    data class TiledCellAgglomerate(val tiledCell: TiledMapTileLayer.Cell, val tileLayer: TileLayer?){
        fun getPropertyInALayerAsString(property: String) : String{
            return tiledCell.tile?.properties?.get(property) as String
        }

        fun cellHasProperty(property:String): Boolean {
            return tiledCell.tile?.properties?.get(property) != null
        }
    }

    private fun createActorsAndLocationsForLayer(tiledLayer: TiledMapTileLayer, tiledMap: TiledMap) {
        for (x in 0..tiledLayer.width) {
            for (y in 0..tiledLayer.height) {
                val cell = tiledLayer.getCell(x, y) ?: continue
                val clickListeningActor = tileMapClickListenerActorFactory.createTileMapActor(this.tiledMapProvider.tiledMap, tiledLayer, cell, TileLocation(x, y)
                )
                clickListeningActor.setBounds(x * tiledLayer.tileWidth, y * tiledLayer.tileHeight, tiledLayer.tileWidth,
                        tiledLayer.tileHeight)
                val location = TileLocation(x, y)
                val tile = LogicalTile(cell.tile, location, clickListeningActor,
                        tiledMapInterpreter.getAllTilesAtXY(tiledMap, TileLocation(x, y)))
                tile.fogOfWarTileActor = generateFogOfWarTileActor(tile)

                logicalTileTracker.addTile(tile)
                addActor(clickListeningActor)
                addActor(tile.fogOfWarTileActor)
                tiledMapInterpreter.validateTileMap(tiledMap)
                tiledMapInterpreter.initializeTileEntities(tiledMap, location)
                val eventListener = tileMapClickListenerFactory.create(clickListeningActor)
                clickListeningActor.addListener(eventListener)
            }
        }
    }

    val whiteTile = ImageIcon("simple", "white_color.png", hittable = false)
    private fun generateFogOfWarTileActor(tile: LogicalTile): Actor {
        val imageActor = whiteTile.toActorWrapper().actor
        val configuredActor = tile.clickListeningActor
        imageActor.setBounds(configuredActor.x, configuredActor.y, configuredActor.width, configuredActor.height)
        imageActor.color = Color.BLACK
        imageActor.color.a = .5f
        imageActor.setFunctionalName(ActorName(ActorOrdering.FOG_OF_WAR))
        return imageActor
    }

    override fun keyUp(keycode: Int): Boolean {
        val tiledMap = tiledMapProvider.tiledMap
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
            orthographicCamera.translate(-32f, 0f)
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
            orthographicCamera.translate(32f, 0f)
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W)
            orthographicCamera.translate(0f, 32f)
        if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
            orthographicCamera.translate(0f, -32f)
        if (keycode == Input.Keys.NUM_1)
            tiledMap.layers.get(0).isVisible = !tiledMap.layers.get(0).isVisible
        if (keycode == Input.Keys.NUM_2)
            tiledMap.layers.get(1).isVisible = !tiledMap.layers.get(1).isVisible
        if (keycode == Input.Keys.TAB){
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.CycleUnitCarousel())
        }
        return false
    }


    override fun scrolled(amount: Int): Boolean{
        if (amount == 1){
            orthographicCamera.zoom *= 1.1f
        }else{
            orthographicCamera.zoom /= 1.1f
        }
        return false
    }
}

public fun List<TiledMapStage.TiledCellAgglomerate>.getTileByLayer(tileLayer: TileLayer) : TiledMapStage.TiledCellAgglomerate? {
    return this.filter { it.tileLayer == tileLayer }.firstOrNull()
}
