package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.controller.TacticalMapController
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.scene2d.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Aaron on 3/30/2018.
 */

//val mySkin: Skin = Skin(Gdx.files.internal("tactical-ui/vis/skin/x2/uiskin.json"))
val mySkin : Skin? = null
@Singleton
class TacMapHudFactory @Inject constructor(val eventNotifier: EventNotifier,
                                           val tacticalMapState: TacticalMapState) {
    fun create(viewPort: Viewport): TacMapHud {
        return TacMapHud(viewPort, eventNotifier, tacticalMapState)
    }
}

@Singleton
class TacMapHud(viewPort: Viewport,
                val eventNotifier: EventNotifier,
                val tacticalMapState: TacticalMapState) : Stage(viewPort), EventListener {
    init{
        VisUI.load();
    }
    override fun consumeEvent(event: TacticalGuiEvent) {
        when (event) {
            is TacticalGuiEvent.CharacterSelected -> {
                selectedUnitDescription?.setText(describeCharacter(event.character))
                selectedUnitDescription?.invalidate()
                selectedUnitDescription?.pack()
            }
        }
    }
    lateinit var window: VisWindow

    private fun describeCharacter(character: LogicalCharacter): String {
        return "Moves per turn: $character.tacMapUnit.movesPerTurn"
    }

    var selectedUnitDescription: VisLabel? = null

    init {
        eventNotifier.registerListener(this)

        this.isDebugAll = true


        val actor =
                VisWindow("UI Window").let {
                    it.width = 240f
                    it.height = 400f
                    it.add(missionObjectivesLabel()).prefWidth(width)
                    it.row()
                    it.add(selectedUnitDescription()).fill().expand()
                    it
                }
        window = actor

        actor.x = 650f
        actor.y = 500f


        this.addActor(actor)
    }

    private fun missionObjectivesLabel(): Label {
        val label = VisLabel("Mission Objectives")
        return label
    }
    private fun selectedUnitDescription(): Label {
        val label = VisLabel("")
        label.setWrap(true)
        selectedUnitDescription = label
        return label
    }
}