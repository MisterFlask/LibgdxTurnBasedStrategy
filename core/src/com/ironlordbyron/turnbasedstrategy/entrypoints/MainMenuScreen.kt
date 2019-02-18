package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import com.kotcrab.vis.ui.building.utilities.Alignment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class MainMenuScreen @Inject constructor(val tacticalMapScreen: TacticalMapScreen): ScreenAdapter(){

    val viewport: ScreenViewport
    val stage: Stage
    init{
        val eventNotifier = GameModuleInjector.getEventNotifier()
        viewport = ScreenViewport()
        stage = Stage(viewport)
        val title = Label("Title Screen", DEFAULT_SKIN)
        title.setAlignment(Align.center)
        title.setY((Gdx.graphics.height * 2 / 3).toFloat())
        title.setWidth(Gdx.graphics.width.toFloat())
        stage.addActor(title)

        val table = Table()

        table.add(playScenarioButton(eventNotifier,
                TacticalGuiEvent.SwapToTacticsScreen(),
                "Default Scenario",
                Scenarios.DEFAULT_SCENARIO))
        table.row()
        table.add(playScenarioButton(eventNotifier,
                TacticalGuiEvent.SwapToTacticsScreen(),
                buttonName = "Partially-procedurally-generated",
                scenarioParams = Scenarios.PARTIAL_PROCEDURAL_SCENARIO))
        table.setPosition(Gdx.graphics.width / 2 - table.getWidth() / 2, Gdx.graphics.height / 2 - table.getHeight() / 2, Alignment.CENTER.alignment)
        stage.addActor(table)
    }

    private fun playScenarioButton(eventNotifier: EventNotifier,
                                   tacticalGuiEventToSend: TacticalGuiEvent,
                                   buttonName: String,
                                   scenarioParams: ScenarioParams) : Button {
        val playButton = TextButton(buttonName, DEFAULT_SKIN)
        playButton.setWidth((Gdx.graphics.width / 2).toFloat())
        playButton.addListener(object : InputListener() {
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                tacticalMapScreen.scenarioStart(scenarioParams)
                eventNotifier.notifyListenersOfGuiEvent(tacticalGuiEventToSend)
            }

            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })
        return playButton
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        super.resize(width, height)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act()
        stage.draw()
    }

}