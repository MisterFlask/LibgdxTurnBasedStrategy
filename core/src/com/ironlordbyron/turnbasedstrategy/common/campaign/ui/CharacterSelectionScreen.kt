package com.ironlordbyron.turnbasedstrategy.common.campaign.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ironlordbyron.turnbasedstrategy.common.CharacterDisplayUiElement
import com.ironlordbyron.turnbasedstrategy.common.EquipmentSlot
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.campaign.CharacterAndEquipmentRoster
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
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
import com.kotcrab.vis.ui.building.utilities.Alignment
import java.util.*
import kotlin.collections.ArrayList

public class CharacterSelectionScreen: ScreenAdapter(){

    private lateinit var scenarioParams: ScenarioParams
    val roster by LazyInject(CharacterAndEquipmentRoster::class.java)
    val characterDisplayTable = CharacterDisplayUiElement()
    val characterListTable = Table()
    val characterEquipmentTable = Table()
    val viewport = ScreenViewport()
    val stage: Stage = Stage(viewport)
    val characterSelectors = ArrayList<CharacterSelector>()
    val deploymentSlots = arrayListOf(DeploymentSlotViewModel(), DeploymentSlotViewModel(), DeploymentSlotViewModel())
    val table = Table()

    // view model shit
    var selectedEquipmentSlot: EquipmentSlot? = null

    init{
        this.deploymentSlots.first().selected = true
        regenerateUi()
        stage.addActor(table)
    }

    public fun regenerateUi(){
        table.clear()
        table.withBorder()
        table.clampToScreenRatio(ActorDimensions(
                .05f, .95f, .95f, .05f
        ))
        table.row()
        table.add(primaryRow())
    }

    private fun generateWeaponSlotsTable(): Table{
        val table = Table()
        val selected = this.deploymentSlots.firstOrNull{it.selected}
        if (selected == null) {
            return table
        }
        val character = selected.character
        if (character == null){
            return table
        }
        for (slot in character.equipmentSlots){
            table.add(WeaponSlot(slot, this))
            table.row()
        }
        return table
    }

    private fun primaryRow(): Table {
        val table = Table()
        table.add(DeploymentSlotsHolder(deploymentSlots, this))
                .fill(0f, 1f).width(150f).align(Alignment.LEFT.alignment)

        if (this.deploymentSlots.first{it.selected}.character == null){
            addTableForCharacterSelect()
        }else{
            table.add(generateWeaponSlotsTable()).width(250f).align(Alignment.LEFT.alignment)
            table.add(generateTableForEquipmentSelect()).width(250f).align(Alignment.LEFT.alignment)
        }
        characterDisplayTable.regenerateCharacterDisplayTable()
        table.add(characterDisplayTable).width(150f)
        table.row()
        table.addButton("Start Mission"){
            startGame()
        }
        return table
    }

    private fun generateTableForEquipmentSelect() : Table {
        populateEquipmentTable()
        return (characterEquipmentTable)
    }

    private fun addTableForCharacterSelect() {
        table.add(characterListTable).width(250f)
        populateCharacterSelectTable()
        characterDisplayTable.withBorder()
    }

    private fun populateEquipmentTable(){
        characterEquipmentTable.clear()
        val unattachedEquipment = roster.unusedEquipment
        for (equipment in unattachedEquipment){
            characterEquipmentTable.add(EquipmentSelector(equipment, this)).width(200f)
            characterEquipmentTable.row()
        }
    }

    private fun populateCharacterSelectTable() {
        characterSelectors.clear()
        characterListTable.clear()
        for (rosterChar in this.roster.characters){
            val selector= CharacterSelector(rosterChar)
            selector.addClickListener {
                characterDisplayTable.selectedCharacter = selector.character
                characterDisplayTable.regenerateCharacterDisplayTable()
            }
            selector.addDoubleClickListener{
                this.deploymentSlots.first{it.selected}.character = selector.character
                this.regenerateUi()
            }
            characterSelectors.add(selector)
            characterListTable.add(selector).width(150f).align(Align.center)
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


class EquipmentSelector(val equipment: LogicalEquipment,
                        val characterSelectionScreen: CharacterSelectionScreen): Table(){
    // if it's in use, show character that's using it

    var selected = false
    var characterUsing: TacMapUnitTemplate? = null
    init{
        val characterUsing = characterUsing
        if (characterUsing != null){
            this.add(characterUsing.tiledTexturePath.toActorWrapper().actor).width(40f).height(40f)
        } else{
            this.add(Table()).width(40f).height(40f) // basically just a dummy to ensure consistent spacing
        }
        this.addLabel(equipment.name,
                afterCreation = {cell -> cell.width(150f).height(40f)})

        this.add(equipment.protoActor.toActorWrapper().actor).width(40f).height(40f)

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

    fun refresh(){
        characterUsing = equipment.characterUsing()
    }
}

class CharacterSelector(val character: TacMapUnitTemplate): Table() {
    var selected: Boolean = false

    init{
        this.add(this.character.tiledTexturePath.toActorWrapper().actor).width(40f).height(40f)
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
    this.touchable = Touchable.enabled
    this.addListener(FlexibleClickListener(func))
}

fun Actor.addDoubleClickListener(func: ()->Unit){
    this.touchable = Touchable.enabled
    this.addListener(DoubleClickListener(func))
}

class DoubleClickListener(val func: () -> Unit) : ClickListener() {
    override fun clicked(event: InputEvent?, x: Float, y: Float) {
        super.clicked(event, x, y)
        if (this.tapCount > 1){
            func()
        }
    }
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




val roster by LazyInject(CharacterAndEquipmentRoster::class.java)
fun LogicalEquipment.isNotInUse(): Boolean {
    return roster.unusedEquipment.map{it.uuid}.contains(this.uuid)
}
fun LogicalEquipment.isInUse(): Boolean{
    return !this.isNotInUse()
}
fun LogicalEquipment.characterUsing() : TacMapUnitTemplate? {
    for (character in roster.characters){
        if (character.equipment.contains(this)){
            return character
        }
    }
    return null
}

class DeploymentSlotsHolder(val slots: MutableList<DeploymentSlotViewModel>, val screen: CharacterSelectionScreen) : Table() {
    init {
        regenerate()
    }

    fun regenerate() {
        clear()
        for (slot in slots) {
            this.add(DeploymentSlot(slot, screen)).width(150f)
            this.row()
        }
    }
}

class WeaponSlot(val equipmentSlot: EquipmentSlot,
                 var characterSelectionScreen: CharacterSelectionScreen) : Table() {
    init{
        regenerate()
    }

    fun regenerate(){
        this.clear()
        val currentEquipment = equipmentSlot.currentEquipment
         if (currentEquipment == null){
             this.addLabel("No Equipment Selected!", afterCreation={it.width(150f)})
         }else{
             this.addLabel(currentEquipment.name, afterCreation={it.width(150f)})
         }

        if (this.equipmentSlot == characterSelectionScreen.selectedEquipmentSlot){
            this.withOrangeBorder()
        }else{
            this.withBorder()
        }

        this.addClickListener {
            characterSelectionScreen.selectedEquipmentSlot = this.equipmentSlot
            characterSelectionScreen.regenerateUi()
        }
    }
}

class DeploymentSlot(val slot: DeploymentSlotViewModel, val screen: CharacterSelectionScreen): Table(){
    init{
        regenerate()
        this.addClickListener {
            screen.deploymentSlots.forEach{it.selected = false}
            this.slot.selected = true;
            screen.regenerateUi() }
    }

    fun regenerate(){
        this.clear()
        val character = slot.character
        if (character == null){
            this.addLabel("No Character Selected", afterCreation = {it.width(150f)})
        }else{
            this.addLabel(character.templateName, afterCreation = {it.width(100f)})
            this.add(character.tiledTexturePath.toActorWrapper().actor).width(50f)
        }
        if (slot.selected){
            this.withOrangeBorder()
        }else{
            this.withBorder()
        }
    }
}

data class DeploymentSlotViewModel(var character: TacMapUnitTemplate? = null, var selected: Boolean = false)


class ObservableWrapper(): Observable() {

    fun notify(item: Any){
        super.setChanged()
        super.notifyObservers(item)
    }

    fun purge(){
        this.deleteObservers()
    }

}