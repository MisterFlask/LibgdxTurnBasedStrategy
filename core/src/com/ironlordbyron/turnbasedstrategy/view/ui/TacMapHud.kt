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
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import com.ironlordbyron.turnbasedstrategy.view.images.FileImageRetriever
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActorFactory
import javax.inject.Inject
import javax.inject.Singleton
import com.badlogic.gdx.utils.Scaling
import com.ironlordbyron.turnbasedstrategy.controller.BoardInputState
import com.ironlordbyron.turnbasedstrategy.view.ui.external.BackgroundColor
import com.kotcrab.vis.ui.building.utilities.Alignment


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
    var hoveredAbility: LogicalAbility? = null
    var boardInputState : BoardInputState = BoardInputState.DefaultState()

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
                regenerateTable()
            }
            is TacticalGuiEvent.SwitchedGuiState -> {
                boardInputState = event.guiState
                regenerateTable()
            }
        }
    }

    private val buttonDimensions = Dimensions(55, 55)

    private fun actionButton(ability : LogicalAbility): Actor? {

        val button = TextButton(ability.name, mySkin)

        val clickListener = object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventNotifier.notifyListeners(TacticalGuiEvent.ClickedButtonToActivateAbility(ability))
                super.clicked(event, x, y)
            }

            override fun enter(event: InputEvent?, x: Float, y:Float, pointer: Int, fromActor: Actor?){
                eventNotifier.notifyListeners(TacticalGuiEvent.StartedHoveringOverAbility(ability))
                hoveredAbility = ability
                abilityTextArea.setText("${ability.name}: ${ability.description}")
                super.enter(event, x, y, pointer, fromActor)
            }
            override fun exit(event: InputEvent?, x: Float, y:Float, pointer: Int, fromActor: Actor?){
                eventNotifier.notifyListeners(TacticalGuiEvent.StoppedHoveringOverAbility(ability))
                hoveredAbility = null
                abilityTextArea.setText("")
                super.exit(event, x, y, pointer, fromActor)
            }
        }
        button.addListener(clickListener)
        return button
    }

    private fun showAbility(ability: LogicalAbility) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var window: Window

    private fun describeCharacter(character: LogicalCharacter): String {
        return "Moves per turn: $character.tacMapUnit.movesPerTurn"
    }
    var selectedUnitDescription: Label? = null
    val portraitDimensions: Dimensions = Dimensions(150,150)

    var debugTextArea: Label = Label("", mySkin)
    var abilityTextArea: Label = Label("", mySkin)
    val characterDisplayTable : Table = Table(mySkin)

    private fun regenerateTable(){
        abilityTextArea = Label("", mySkin)
        val backgroundColor = backgroundColor()
        characterDisplayTable.setBackground(backgroundColor)
        var selectedCharacter: LogicalCharacter? = selectedCharacter
        characterDisplayTable.clearChildren()
        if (selectedCharacter != null){
            characterDisplayTable.add(Label(selectedCharacter.tacMapUnit.templateName, mySkin, "title"))
            characterDisplayTable.row()
            characterDisplayTable.add(characterImageManager.retrieveCharacterImage(selectedCharacter))
                    .size(portraitDimensions.width.toFloat(),portraitDimensions.height.toFloat())
            characterDisplayTable.row()
            characterDisplayTable.add(displayCharacterHp(selectedCharacter))
        }
        // NOTE TO FUTURE SELF: Table controls size of images, DOES NOT RESPECT image preferred size
        characterDisplayTable.row()
        characterDisplayTable.add(Label("", mySkin)).fillY().expandY()
        characterDisplayTable.row()

        if (selectedCharacter != null){
            for (ability in selectedCharacter.abilities){
                characterDisplayTable.add(actionButton(ability))
            }
        }


        debugTextArea.setText(debugTextAreaText())
        debugTextArea.setWrap(true)
        debugTextArea.setAlignment(Alignment.CENTER.alignment)

        characterDisplayTable.row()
        characterDisplayTable.add(endTurnButton())
        characterDisplayTable.row()
        characterDisplayTable.add(abilityTextArea)

        characterDisplayTable.row()
        characterDisplayTable.add(debugTextArea).width(300f)
    }

    private fun displayCharacterHp(selectedCharacter: LogicalCharacter): Label {
        return Label("HP: ${selectedCharacter.healthLeft}/${selectedCharacter.maxHealth}", mySkin)
    }

    private fun debugTextAreaText(): String {
        return boardInputState.name
    }

    private fun backgroundColor(): BackgroundColor {
        val backgroundColor = BackgroundColor("simple/white_color.png")
        backgroundColor.setColor(0, 0, 0, 166)
        return backgroundColor
    }

    init {
        eventNotifier.registerListener(this)

        // this.isDebugAll = true


        val actor =
                Window("", mySkin).let {
                    it.width = 440f
                    it.height = 600f
                    it.add(characterDisplayTable).fill().expand()
                    it
                }
        window = actor
        val background = Image(mySkin, "bg")
        background.setScaling(Scaling.stretch)
        background.setFillParent(true)
        window.addActor(background)
        background.toBack()

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
