package com.ironlordbyron.turnbasedstrategy.common.campaign.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ironlordbyron.turnbasedstrategy.common.CharacterDisplayUiElement
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.campaign.CharacterRoster
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.addHoverLighting
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.entrypoints.TacticalMapScreen
import com.ironlordbyron.turnbasedstrategy.entrypoints.log
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.guice.eventNotifier
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.ActorDimensions
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.clampToScreenRatio
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import com.ironlordbyron.turnbasedstrategy.view.ui.addLabel
import com.ironlordbyron.turnbasedstrategy.view.ui.withBorder
import com.ironlordbyron.turnbasedstrategy.view.ui.withOrangeBorder
import kotlin.collections.ArrayList

public class CharacterSelectionScreen: ScreenAdapter(){

    private lateinit var scenarioParams: ScenarioParams
    val roster by LazyInject(CharacterRoster::class.java)
    val characterDisplayTable = CharacterDisplayUiElement()
    val characterListTable = Table()
    val viewport = ScreenViewport()
    val stage: Stage = Stage(viewport)
    val characterSelectors = ArrayList<CharacterSelector>()
    init{
        val table = Table()
        table.withBorder()
        table.clampToScreenRatio(ActorDimensions(
.2f, .8f, .8f, .2f
        ))
        table.addLabel("CHARACTER SELECTION")
        table.row()
        populateCharacterListTable()
        table.add(characterListTable)
        table.add(characterDisplayTable)
        characterDisplayTable.selectedCharacter = roster.characters.first()
        characterDisplayTable.regenerateCharacterDisplayTable()
        characterDisplayTable.withBorder()
        table.row()
        table.addButton("Start Mission"){
            startGame()
        }
        stage.addActor(table)
    }

    private fun populateCharacterListTable() {
        characterSelectors.clear()
        characterListTable.clear()
        for (rosterChar in this.roster.characters){
            val selector= CharacterSelector(rosterChar)
            selector.addHoverListener {
                characterDisplayTable.selectedCharacter = selector.character
                characterDisplayTable.regenerateCharacterDisplayTable()
            }
            characterSelectors.add(selector)

            characterListTable.add(selector)
            characterListTable.row()
        }
    }

    val tacMapScreen by LazyInject(TacticalMapScreen::class.java)

    fun initializeCharacterSelectionScreen(scenarioParams: ScenarioParams){
        this.scenarioParams = scenarioParams
    }

    fun startGame(){
        val scenarioParams = scenarioParams.copy(unitsThatPlayerWillDeploy = collectSelectedUnits())
        tacMapScreen.scenarioStart(scenarioParams)
        eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.ScenarioStart(scenarioParams))
        eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.SwapToTacticsScreen())
    }

    private fun collectSelectedUnits(): Collection<TacMapUnitTemplate> {
        return this.characterSelectors
                .filter{it.selected}
                .map{it.character}
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


private val activeColor = Color.RED
private val inactiveColor = Color.WHITE

class CharacterSelector(val character: TacMapUnitTemplate): Table() {
    var selected: Boolean = false

    init{
        this.add(this.character.tiledTexturePath.toActor().actor).width(40f).height(40f)
        this.addLabel(this.character.templateName, skipRow = true)
        this.addHoverLighting()
        this.addClickListener {
            log("Hit click listener on char select")
            this.selected = !this.selected
            if (this.selected){
                this.withOrangeBorder()
            } else {
                this.withBorder()
            }
        }
        this.withBorder()
        this.touchable = Touchable.enabled

    }
}

fun Actor.addClickListener(func: () -> Unit){
    this.addListener(FlexibleClickListener(func))
}

private class FlexibleClickListener(val func: () -> Unit) : ClickListener() {
    override fun clicked(event: InputEvent?, x: Float, y: Float) {
        super.clicked(event, x, y)
        func()
    }
}

fun Actor.addHoverListener(func: () -> Unit){
    this.addListener(FlexibleHoverListener(func))
}

private class FlexibleHoverListener(val func: () -> Unit) : InputListener() {
    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        super.enter(event, x, y, pointer, fromActor)
        func()
    }
}

fun Table.addButton(text: String, init: (Button)->Unit = {}, action: () -> Unit){
    val button = Button(DEFAULT_SKIN)
    button.add(text)
    button.addClickListener(action)
    this.add(button)
    init(button)
}
