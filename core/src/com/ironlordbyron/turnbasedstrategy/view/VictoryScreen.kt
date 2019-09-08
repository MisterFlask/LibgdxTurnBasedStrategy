package com.ironlordbyron.turnbasedstrategy.view

import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ironlordbyron.turnbasedstrategy.common.campaign.ui.addButton
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.ui.addLabel

public class VictoryScreen() : ScreenAdapter() {
    // victory screen needs to show off what the player just did, the enemies that were killed, player casualties.
    // preferable if it were in a nice format, but fuckit

    val masterTable = Table()
    val viewport = ScreenViewport()
    val stage: Stage = Stage(viewport)

    init{
        stage.addActor(masterTable)

        populateMasterTable()
    }

    val eventNotifier by LazyInject(EventNotifier::class.java)

    fun populateMasterTable(){
        masterTable.addLabel("Victory!")

        masterTable.addButton("Continue"){
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.SwapToMainMenu())

        }
    }
}