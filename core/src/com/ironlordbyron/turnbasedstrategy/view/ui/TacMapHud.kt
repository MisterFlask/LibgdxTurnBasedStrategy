package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.Viewport
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.extensions.toImage
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import com.ironlordbyron.turnbasedstrategy.view.images.FileImageRetriever
import com.ironlordbyron.turnbasedstrategy.view.images.Icon
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActorFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Aaron on 3/30/2018.
 */

val mySkin: Skin = Skin(Gdx.files.internal("tactical-ui-skins/Tracer_UI_Skin/tracerui/tracer-ui.json"))
//val mySkin : Skin? = null
@Singleton
class TacMapHudFactory @Inject constructor(val eventNotifier: EventNotifier,
                                           val tacticalMapState: TacticalMapState,
                                           val spriteActorFactory: SpriteActorFactory,
                                           val fileImageRetriever: FileImageRetriever,
                                           val characterImageManager: CharacterImageManager) {
    fun create(viewPort: Viewport): TacMapHud {
        return TacMapHud(viewPort, eventNotifier, tacticalMapState, spriteActorFactory, fileImageRetriever, characterImageManager)
    }
}

@Singleton
class TacMapHud(viewPort: Viewport,
                val eventNotifier: EventNotifier,
                val tacticalMapState: TacticalMapState,
                val spriteActorFactory: SpriteActorFactory,
                val fileImageRetriever: FileImageRetriever,
                val characterImageManager: CharacterImageManager) : Stage(viewPort), EventListener {
    var selectedCharacter: LogicalCharacter? = null

    override fun consumeEvent(event: TacticalGuiEvent) {
        when (event) {
            is TacticalGuiEvent.CharacterSelected -> {
                selectedUnitDescription?.setText(describeCharacter(event.character))
                selectedUnitDescription?.invalidate()
                selectedUnitDescription?.pack()
                this.selectedCharacter = event.character
                regenerateTable()
            }
            is TacticalGuiEvent.CharacterUnselected -> {

                this.selectedCharacter = null
            }
        }
    }

    private val buttonDimensions = Dimensions(55, 55)

    private fun actionButton(ability : LogicalAbility): Actor? {
        val button = TextButton(ability.name, mySkin)

        val clickListener = object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                println("Clicked button for glorious ability ${ability.name}")
                super.clicked(event, x, y)
            }
        }
        button.addListener(clickListener)
        return button
    }

    lateinit var window: Window

    private fun describeCharacter(character: LogicalCharacter): String {
        return "Moves per turn: $character.tacMapUnit.movesPerTurn"
    }
    var selectedUnitDescription: Label? = null
    val portraitDimensions: Dimensions = Dimensions(150,150)

    val table : Table = Table()
    private fun regenerateTable(){
        var selectedCharacter: LogicalCharacter? = selectedCharacter
        table.clearChildren()
        if (selectedCharacter != null){
            table.add(characterImageManager.retrieveCharacterImage(selectedCharacter))
                    .size(portraitDimensions.width.toFloat(),portraitDimensions.height.toFloat())
        }
        table.add(fileImageRetriever.retrieveIconImage(Icon.ASSAULT).toImage())
                .size(portraitDimensions.width.toFloat(),portraitDimensions.height.toFloat())
        // NOTE TO FUTURE SELF: Table controls size of images, DOES NOT RESPECT image preferred size
        table.row()

        if (selectedCharacter != null){
            for (ability in selectedCharacter.abilities){
                table.add(actionButton(ability))
            }
        }
        table.row()
        table.add(endTurnButton())
        table.add(endTurnButton())
    }

    init {
        eventNotifier.registerListener(this)

        // this.isDebugAll = true


        val actor =
                Window("", mySkin).let {
                    it.width = 440f
                    it.height = 600f
                    it.add(table)

                    it
                }
        window = actor

        actor.x = 650f
        actor.y = 500f


        this.addActor(actor)
    }

    private fun missionObjectivesLabel(): Label {
        val label = Label("Mission Objectives", mySkin)
        return label
    }
    private fun selectedUnitDescription(): Label {
        val label = Label("", mySkin)
        label.setWrap(true)
        selectedUnitDescription = label
        return label
    }
    private fun endTurnButton() : Button {
        val button = TextButton("End Turn", mySkin)
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
