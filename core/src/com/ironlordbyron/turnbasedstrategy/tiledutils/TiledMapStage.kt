package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.google.inject.assistedinject.Assisted
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TempBattleStarter
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Singleton


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