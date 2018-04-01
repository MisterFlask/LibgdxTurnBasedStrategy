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
import ktx.scene2d.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Aaron on 3/30/2018.
 */
val mySkin: Skin = Skin(Gdx.files.internal("tactical-ui/neonui/neonui/neon-ui.json"))

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
    override fun consumeEvent(event: TacticalGuiEvent) {
        when (event) {
            is TacticalGuiEvent.CharacterSelected -> {
                selectedUnitDescription?.setText(describeCharacter(event.character))
                selectedUnitDescription?.setScale(3.5f)
            }
        }
    }

    private fun describeCharacter(character: LogicalCharacter): String {
        return "Moves per turn: $character.tacMapUnit.movesPerTurn"
    }

    var selectedUnitDescription: Label? = null

    init {
        eventNotifier.registerListener(this)

        this.isDebugAll = true
        Scene2DSkin.defaultSkin = mySkin

        val actor =
                table {
                    setFillParent(true)

                    add(label("Hello world!")).top().left()
                    row()
                    add(label("2asdfasdfas3 asdfasdf asdf asdfasdf asdf asdf asdf asdf") {
                        setWrap(true)
                        selectedUnitDescription = this
                    }).width(300f)
                    row().expandY()
                    add(button { button ->
                        label("End Turn")
                        this.addListener(object : ClickListener() {
                            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                eventNotifier.notifyListeners(TacticalGuiEvent.EndTurnButtonClicked())
                            }
                        });
                    }).left()
                }
        actor.x = 300f
        actor.width = 300f

        this.addActor(actor)

    }
}