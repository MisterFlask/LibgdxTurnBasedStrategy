package com.ironlordbyron.turnbasedstrategy.common.campaign.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.ironlordbyron.turnbasedstrategy.common.CharacterDisplayUiElement
import com.ironlordbyron.turnbasedstrategy.common.EquipmentSlot
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.campaign.CharacterAndEquipmentRoster
import com.ironlordbyron.turnbasedstrategy.common.campaign.EquipmentWithQuantity
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
import com.ironlordbyron.turnbasedstrategy.view.ui.*
import com.kotcrab.vis.ui.building.utilities.Alignment
import java.util.*
import kotlin.collections.ArrayList

public class CharacterSelectionScreen: ScreenAdapter(){

    private lateinit var scenarioParams: ScenarioParams
    val roster by LazyInject(CharacterAndEquipmentRoster::class.java)
    val characterEquipmentTable = Table()
    val viewport = ScreenViewport()
    val stage: Stage = Stage(viewport)

    // specialty tables
    val characterSelectors = ArrayList<CharacterSelector>()
    val characterDisplayTable = CharacterDisplayUiElement()
    val equipmentSelectors = ArrayList<EquipmentSelector>()

    val characterDeploymentSlots = arrayListOf(DeploymentSlotViewModel(), DeploymentSlotViewModel(), DeploymentSlotViewModel())
    val masterTable = Table()

    // view model shit
    var selectedEquipmentSlot: EquipmentSlot? = null

    init{
    }

    public fun regenerateUi(){
        equipmentSelectors.clear()
        masterTable.clear()
        masterTable.withBorder()
        masterTable.clampToScreenRatio(ActorDimensions(
                .05f, .95f, .95f, .05f
        ))
        masterTable.row()
        masterTable.add(primaryRow()).expand().fill()
    }

    private fun generateWeaponSlotsTable(): Table{
        val table = Table()
        val selected = this.characterDeploymentSlots.firstOrNull{it.selected}
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
        table.add(DeploymentSlotsHolder(characterDeploymentSlots, this))
                .fill(0f, 1f).width(150f).align(Alignment.LEFT.alignment)

        if (this.characterDeploymentSlots.first{it.selected}.character == null){
            table.add(buildTableForCharacterSelect()).width(250f).left()
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

    private fun buildTableForCharacterSelect(): Table {
        val characterListTable = Table()
        characterSelectors.clear()
        characterListTable.clear()

        val charactersSelected = characterDeploymentSlots
                .filter { it.character != null }
                .map{it.character!!}
        val charactersThatCanBeSelected = this.roster.characters
                .filter{!charactersSelected.contains(it)}
        for (rosterChar in charactersThatCanBeSelected){
            val selector= CharacterSelector(rosterChar)
            selector.addClickListener {
                characterDisplayTable.selectedCharacter = selector.character
                characterDisplayTable.regenerateCharacterDisplayTable()
            }
            selector.addDoubleClickListener{
                this.characterDeploymentSlots.first{it.selected}.character = selector.character
                this.regenerateUi()
            }
            characterSelectors.add(selector)
            characterListTable.add(selector).width(150f).align(Align.center)
            characterListTable.row()
        }
        return characterListTable
    }

    private fun populateEquipmentTable(){
        characterEquipmentTable.clear()
        if (this.selectedEquipmentSlot == null) return
        val unattachedEquipment = roster.unusedEquipment
        val selectedEquipmentSlot = this.selectedEquipmentSlot!!

        for (equipment in unattachedEquipment){
            if (!selectedEquipmentSlot.isEquipmentAllowed(equipment.equipment)){
                continue
            }
            characterEquipmentTable.add(EquipmentSelector(equipment, this)).width(200f)
            characterEquipmentTable.row()

        }
    }

    private fun populateCharacterSelectTable() {
    }

    val tacMapScreen by LazyInject(TacticalMapScreen::class.java)

    fun initializeCharacterSelectionScreen(scenarioParams: ScenarioParams){
        this.scenarioParams = scenarioParams
        roster.initializeCharacterLoadouts()
        this.characterDeploymentSlots.first().selected = true
        regenerateUi()
        stage.addActor(masterTable)
    }

    fun startGame(){
        val scenarioParams = scenarioParams.copy(unitsThatPlayerWillDeploy = collectSelectedUnits())
        tacMapScreen.scenarioStart(scenarioParams)
        eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.SwapToTacticsScreen())
    }

    private fun collectSelectedUnits(): Collection<TacMapUnitTemplate> {
        val charactersSelected = characterDeploymentSlots
                .filter { it.character != null }
                .map{it.character!!}
        return charactersSelected
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


class EquipmentSelector(val equipment: EquipmentWithQuantity,
                        val characterSelectionScreen: CharacterSelectionScreen): Table(){
    // if it's in use, show character that's using it

    var selected = false
    var characterUsing: TacMapUnitTemplate? = null
    init{
        characterSelectionScreen.equipmentSelectors.add(this)
        val characterUsing = characterUsing
        if (characterUsing != null){
            this.add(characterUsing.tiledTexturePath.toActorWrapper().actor).width(40f).height(40f)
        } else{
            this.add(Table()).width(40f).height(40f) // basically just a dummy to ensure consistent spacing
        }
        this.addLabel(equipment.equipment.name,
                afterCreation = {cell -> cell.width(150f).height(40f)})

        this.addSubtitleLabel(equipment.quantity.toString())
        this.add(equipment.equipment.protoActor.toActorWrapper().actor).width(40f).height(40f)

        this.addClickListener {
            characterSelectionScreen.equipmentSelectors.forEach{it.selected = false; it.refreshAppearance()}
            log("Hit click listener on char select")
            this.selected = true
            this.refreshAppearance()
        }
        this.addDoubleClickListener {
            characterSelectionScreen.selectedEquipmentSlot!!.currentEquipment = this.equipment.equipment;
            characterSelectionScreen.regenerateUi()
        }
        this.withBorder()
        this.touchable = Touchable.enabled
    }

    fun refreshAppearance(){
        if (this.selected){
            this.withOrangeBorder()
        } else {
            this.withBorder()
        }

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

fun Table.addButton(text: String, init: (Button)->Unit = {}, action: () -> Unit): Cell<Button> {
    val button = Button(DEFAULT_SKIN)
    button.add(text)
    button.addClickListener(action)
    init(button)
    return this.add(button)
}




val roster by LazyInject(CharacterAndEquipmentRoster::class.java)
fun LogicalEquipment.isAvailable(): Boolean {
    return roster.unusedEquipment.map{it.equipment.uuid}.contains(this.uuid)
}
fun LogicalEquipment.isInUse(): Boolean{
    return !this.isAvailable()
}

class DeploymentSlotsHolder(val slots: MutableList<DeploymentSlotViewModel>, val screen: CharacterSelectionScreen) : Table() {
    init {
        regenerate()
    }

    fun regenerate() {
        clear()
        for (slot in slots) {
            if (slot.character != null) {
                this.addButton("Remove") {
                    slot.character = null
                    screen.regenerateUi()
                }.width(50f)
            } else {
                this.add(Table()).width(50f)
            }

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
        row()
        this.addSubtitleLabel(equipmentSlot.allowedEquipment.joinToString(separator = "/") { it.toString() })

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
            screen.characterDeploymentSlots.forEach{it.selected = false}
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
            this.add(character.tiledTexturePath.toActorWrapper().actor).width(50f).height(50f)
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