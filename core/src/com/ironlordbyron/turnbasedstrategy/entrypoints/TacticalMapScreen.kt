package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.BlankMapGenerator
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams

public class TacticalMapScreen : ScreenAdapter() {

    var TILEMAP_SCALING_FACTOR: Float = 1.0f // TODO: This super doesn't work, don't change it

    private val WINDOW_WIDTH = 1100
    private val WINDOW_HEIGHT = 600

    internal lateinit var tiledMap: TiledMap
    internal lateinit var tacMapCamera: OrthographicCamera
    internal lateinit var tiledMapRenderer: TiledMapRenderer
    internal lateinit var tiledMapStage: Stage
    internal lateinit var hudStage: Stage
    internal lateinit var hudCamera: OrthographicCamera

    init{

        Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT)
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        tacMapCamera = OrthographicCamera()
        tacMapCamera.setToOrtho(false, w, h)
        tacMapCamera.update()
        GameModuleInjector.initGameCameraProvider(tacMapCamera)

        setupScenario(w, h, Scenarios.DEFAULT_SCENARIO)

        initializeControls()
    }

    private fun setupScenario(w: Float, h: Float, scenarioParams: ScenarioParams) {
        val tileMapGenerator = GameModuleInjector.createTiledMapGenerator()
        tiledMap = tileMapGenerator.generateMap(scenarioParams)
        val tileMapProvider = GameModuleInjector.createGameStateProvider()
        tileMapProvider.tiledMap = tiledMap
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, TILEMAP_SCALING_FACTOR)
        val tiledMapStageFactory = GameModuleInjector.createTiledMapStageFactory()
        tiledMapStage = tiledMapStageFactory.create(tiledMap, tacMapCamera)
        val tiledMapViewport = FitViewport(w, h, tacMapCamera)
        tiledMapStage.viewport = tiledMapViewport
        tiledMapStage.viewport.camera = tacMapCamera
        hudCamera = OrthographicCamera(w, h)
        val tacMapHudFactory = GameModuleInjector.createTacMapHudFactory()
        hudStage = tacMapHudFactory.create(FitViewport(w, h, hudCamera))
        hudCamera.update()
    }

    private fun initializeControls() {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(hudStage)
        multiplexer.addProcessor(tiledMapStage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun show() {
        super.show()
        initializeControls()

        // This is really stupid and I don't know why it is, but I have to call resize in order for input events to process
        // after switching screens.
        // HACK
        this.resize(WINDOW_WIDTH, WINDOW_HEIGHT)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.1f, .1f, .1f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        tacMapCamera.update()
        tiledMapRenderer.setView(tacMapCamera)
        tiledMapRenderer.render()
        tiledMapStage.act()
        GameModuleInjector.getGameCameraProvider().render()
        GameModuleInjector.getSpecialEffectManager().renderSpecialEffects()
        hudCamera.update()
        tiledMapStage.draw()
        hudStage.draw()
        hudStage.act()
    }

    override fun resize(width: Int, height: Int) {
        tiledMapStage.viewport.update(width, height)
        hudStage.viewport.update(width, height)
    }

    override fun dispose() {
        super.dispose()
        hudStage.dispose()
        tiledMapStage.dispose()
    }
}