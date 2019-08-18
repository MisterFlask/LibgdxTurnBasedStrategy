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
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.StageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.MapGenerationApplicator
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams
import com.ironlordbyron.turnbasedstrategy.view.external_deprecated.TacMapEffectsList
import com.ironlordbyron.turnbasedstrategy.view.ui.TacMapTopStatusDisplay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class  TacticalMapScreen @Inject constructor(val eventNotifier: EventNotifier,
                                                    val tiledMapStageProvider: StageProvider,
                                                    val mapGenerationApplicator: MapGenerationApplicator) : ScreenAdapter(){



    val w get() = Gdx.graphics.width.toFloat()
    val h get() = Gdx.graphics.height.toFloat()
    var TILEMAP_SCALING_FACTOR: Float = 1.0f // TODO: This super doesn't work, don't change it

    private val WINDOW_WIDTH = w
    private val WINDOW_HEIGHT = h


     internal lateinit var tiledMap: TiledMap
    internal lateinit var tacMapCamera: OrthographicCamera
    internal lateinit var tiledMapRenderer: TiledMapRenderer
    internal lateinit var tiledMapStage: Stage
    internal lateinit var hudStage: Stage
    internal lateinit var hudCamera: OrthographicCamera
    lateinit var tacMapTopStatusDisplay : TacMapTopStatusDisplay

    init{

        Gdx.graphics.setWindowedMode(WINDOW_WIDTH.toInt(), WINDOW_HEIGHT.toInt())

        tacMapCamera = OrthographicCamera()
        tacMapCamera.setToOrtho(false, w, h)
        tacMapCamera.update()
        GameModuleInjector.initGameCameraProvider(tacMapCamera)
    }

    public fun scenarioStart(scenarioParams: ScenarioParams){

        eventNotifier.notifyListenersOfGameEvent(TacticalGameEvent.INITIALIZE())

        this.tiledMap = initializeTileMapFromScenario(scenarioParams)

        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, TILEMAP_SCALING_FACTOR)
        val tiledMapStageFactory = GameModuleInjector.createTiledMapStageFactory()
        tiledMapStage = tiledMapStageFactory.create(tacMapCamera)
        val tiledMapViewport = FitViewport(w, h, tacMapCamera)
        tiledMapStage.viewport = tiledMapViewport
        tiledMapStage.viewport.camera = tacMapCamera
        hudCamera = OrthographicCamera(w, h)

        val tacMapHudFactory = GameModuleInjector.createTacMapHudFactory()
        hudStage = tacMapHudFactory.create(FitViewport(w, h, hudCamera))

        tacMapTopStatusDisplay = TacMapTopStatusDisplay(FitViewport(w, h, hudCamera))
        hudCamera.update()

        initializeControls()

        tiledMapStageProvider.tiledMapStage.initializeBattle(tiledMap)
        mapGenerationApplicator.generateMapForScenario(scenarioParams)
        eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.ScenarioStart(scenarioParams))
    }

    private fun initializeTileMapFromScenario(scenarioParams: ScenarioParams) : TiledMap{
        val tileMapGenerator = GameModuleInjector.createTiledMapGenerator()
        val tiledMap = tileMapGenerator.generateMap(scenarioParams)
        val tileMapProvider = GameModuleInjector.createGameStateProvider()
        tileMapProvider.tiledMap = tiledMap
        return tiledMap
    }

    private fun initializeControls() {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(hudStage)
        multiplexer.addProcessor(tiledMapStage)
        multiplexer.addProcessor(tacMapTopStatusDisplay)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun show() {
        super.show()
        initializeControls()

        // This is really stupid and I don't know why it is, but I have to call resize in order for input events to process
        // after switching screens.
        // HACK
        this.resize(WINDOW_WIDTH.toInt(), WINDOW_HEIGHT.toInt())
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

        tacMapTopStatusDisplay.draw()
        tacMapTopStatusDisplay.act()
        TacMapEffectsList.update()
    }

    override fun resize(width: Int, height: Int) {
        tiledMapStage.viewport.update(width, height)
        hudStage.viewport.update(width, height)
        tacMapTopStatusDisplay.viewPort.update(width, height)
        tacMapTopStatusDisplay.regenerateTable()
    }

    override fun dispose() {
        super.dispose()
        hudStage.dispose()
        tiledMapStage.dispose()
        tacMapTopStatusDisplay.dispose()
    }

}