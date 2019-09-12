package com.ironlordbyron.turnbasedstrategy.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.campaign.ui.addButton
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.ActorDimensions
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.clampToScreenRatio
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import com.ironlordbyron.turnbasedstrategy.view.ui.addLabel
import com.ironlordbyron.turnbasedstrategy.view.ui.withBorder
import com.ironlordbyron.turnbasedstrategy.view.ui.withGoldBorderBlackBackground
import javax.inject.Inject
import javax.inject.Singleton
import javax.swing.text.StyleContext.DEFAULT_STYLE

// NOTE:  Uses the information from the previous
@Singleton
public class VictoryScreen: ScreenAdapter() {
    // victory screen needs to show off what the player just did, the enemies that were killed, player casualties.
    // preferable if it were in a nice format, but fuckit
    val globalTacMapState by LazyInject(GlobalTacMapState::class.java)
    val tacMapState by LazyInject(TacticalMapState::class.java)

    val masterTable = Table()
    val viewport = ScreenViewport()
    val stage: Stage = Stage(viewport)

    init{
        stage.addActor(masterTable)

        masterTable.withBorder()
        masterTable.clampToScreenRatio(ActorDimensions(
                .05f, .95f, .95f, .05f
        ))
        populateMasterTable()
    }

    val eventNotifier by LazyInject(EventNotifier::class.java)

    fun populateMasterTable(){
        masterTable.clear()
        masterTable.add(Label("Victory?", DEFAULT_SKIN)).uniform().row()
        masterTable.withGoldBorderBlackBackground()

        // casualties section
        masterTable.add(buildScoreTable()).uniform().row()

        // dead section
        masterTable.add(buildDeadTable()).uniform().row()

        masterTable.addButton("Continue"){
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.SwapToMainMenu())
        }.uniform().row()
    }

    private fun buildDeadTable(): Table {
        val table = Table(DEFAULT_SKIN)
        val deadEnemyCharacters = tacMapState.deadCharacters.filter{it.nonMinionEnemy}
        val deadPlayerCharacters = tacMapState.deadCharacters.filter{it.isPlayerCharacter}
        table.add("PERISHED:").row()
        for (pc in deadPlayerCharacters){
            table.add(pc.templateName).row()
        }
        return table
    }

    private fun buildScoreTable(): Table {
        val table = Table(DEFAULT_SKIN)
        table.add("Objectives completed:").row()
        val globState = GameModuleInjector.generateInstance(GlobalTacMapState::class.java)
        for (objective in globState.battleGoals){
            val completeLabel = if (objective.isGoalMet()) "COMPLETE" else "FAILED"
            val label = Label("*${objective.name}- ${objective.description}: [${completeLabel}]", DEFAULT_SKIN)
            label.color = if (objective.isGoalMet()) Color.GREEN else Color.RED
            table.add(label)
        }
        return table
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