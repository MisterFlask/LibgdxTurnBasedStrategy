package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.ActorDimensions
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.clampToScreenRatio
import javax.inject.Singleton


@Singleton
public class MissionSelectScreen: ScreenAdapter() {
    // victory screen needs to show off what the player just did, the enemies that were killed, player casualties.
    // preferable if it were in a nice format, but fuckit
    val globalTacMapState by LazyInject(GlobalTacMapState::class.java)
    val tacMapState by LazyInject(TacticalMapState::class.java)

    val masterTable = Table()
    val viewport = ScreenViewport()
    val stage: Stage = Stage(viewport)

    private val COLUMN_WIDTH = 250f

    init{
        stage.addActor(masterTable)

        masterTable.withBorder()
        populateMasterTable()
    }

    fun populateMasterTable(){
        masterTable.defaults().width(COLUMN_WIDTH)
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