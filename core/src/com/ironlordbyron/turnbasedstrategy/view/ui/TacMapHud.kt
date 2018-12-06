package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.Viewport
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import com.ironlordbyron.turnbasedstrategy.view.images.FileImageRetriever
import com.ironlordbyron.turnbasedstrategy.view.images.Icon
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActorFactory
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisWindow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Aaron on 3/30/2018.
 */

//val mySkin: Skin = Skin(Gdx.files.internal("tactical-ui/vis/skin/x2/uiskin.json"))
val mySkin : Skin? = null
@Singleton
class TacMapHudFactory @Inject constructor(val eventNotifier: EventNotifier,
                                           val tacticalMapState: TacticalMapState,
                                           val spriteActorFactory: SpriteActorFactory,
                                           val fileImageRetriever: FileImageRetriever) {
    fun create(viewPort: Viewport): TacMapHud {
        return TacMapHud(viewPort, eventNotifier, tacticalMapState, spriteActorFactory, fileImageRetriever)
    }
}

@Singleton
class TacMapHud(viewPort: Viewport,
                val eventNotifier: EventNotifier,
                val tacticalMapState: TacticalMapState,
                val spriteActorFactory: SpriteActorFactory,
                val fileImageRetriever: FileImageRetriever) : Stage(viewPort), EventListener {
    init{
        VisUI.load();
    }
    override fun consumeEvent(event: TacticalGuiEvent) {
        when (event) {
            is TacticalGuiEvent.CharacterSelected -> {
                selectedUnitDescription?.setText(describeCharacter(event.character))
                selectedUnitDescription?.invalidate()
                selectedUnitDescription?.pack()
                selectedUnitAbilitiesHolder.addActor(ImageButton())
            }
        }
    }

    private val buttonDimensions = Dimensions(55, 55)

    private fun ImageButton(): Actor? {
        val image=  fileImageRetriever.retrieveIconImageAsDrawable(Icon.ASSAULT, buttonDimensions, color = Color.RED);
        val button = ImageButton(image)

        val clickListener = object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                println("WOOO CLICKED ACTION BUTTON")
                super.clicked(event, x, y)
            }
        }
        button.addListener(clickListener)
        return button
    }

    lateinit var window: VisWindow

    private fun describeCharacter(character: LogicalCharacter): String {
        return "Moves per turn: $character.tacMapUnit.movesPerTurn"
    }

    var selectedUnitDescription: VisLabel? = null
    val selectedUnitAbilitiesHolder: VerticalFlowGroup = VerticalFlowGroup()

    init {
        eventNotifier.registerListener(this)

        this.isDebugAll = true

        val actor =
                VisWindow("UI Window").let {
                    it.width = 440f
                    it.height = 600f
                    it.add(missionObjectivesLabel()).prefWidth(width)
                    it.row()
                    it.add(selectedUnitDescription()).fill().expand()
                    it.row()
                    it.add(getSelectedUnitAbilities()).expand()
                    it.row()
                    it.add(endTurnButton())
                    it
                }
        window = actor

        actor.x = 650f
        actor.y = 500f


        this.addActor(actor)
    }

    private fun getSelectedUnitAbilities(): VerticalFlowGroup {
        return selectedUnitAbilitiesHolder
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
    private fun endTurnButton() : Button {
        val button = VisTextButton("End Turn")
        val clickListener = object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventNotifier.notifyListeners((TacticalGuiEvent.EndTurnButtonClicked()))
                super.clicked(event, x, y)
            }
        }
        button.addListener(clickListener)
        return button
    }
}