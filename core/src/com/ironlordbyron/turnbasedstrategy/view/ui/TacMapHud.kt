package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.Viewport
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import com.ironlordbyron.turnbasedstrategy.view.images.FileImageRetriever
import com.ironlordbyron.turnbasedstrategy.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.tiledutils.SpriteActorFactory
import javax.inject.Inject
import javax.inject.Singleton
import com.badlogic.gdx.utils.Scaling
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.ContextualAbilityFactory
import com.ironlordbyron.turnbasedstrategy.common.wrappers.RenderingFunction
import com.ironlordbyron.turnbasedstrategy.controller.*
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.LogicalCharacterActorGroup
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.PulseAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ImageIcon
import com.ironlordbyron.turnbasedstrategy.view.ui.external.BackgroundColor
import com.kotcrab.vis.ui.building.utilities.Alignment


/**
 * Created by Aaron on 3/30/2018.
 */

public val DEFAULT_SKIN: Skin = Skin(Gdx.files.internal("tactical-ui-skins/Tracer_UI_Skin/tracerui/tracer-ui.json"))
@Singleton
class TacMapHudFactory @Inject constructor(val eventNotifier: EventNotifier,
                                           val tacticalMapState: TacticalMapState,
                                           val spriteActorFactory: SpriteActorFactory,
                                           val fileImageRetriever: FileImageRetriever,
                                           val characterImageManager: CharacterImageManager,
                                           val boardInputStateProvider: BoardInputStateProvider,
                                           val logicalTileTracker: LogicalTileTracker,
                                           val contextualAbilityFactory: ContextualAbilityFactory,
                                           val pulseAnimationGenerator: PulseAnimationGenerator,
                                           val textLabelGenerator: TextLabelGenerator) {
    fun create(viewPort: Viewport): TacMapHud {
        return TacMapHud(viewPort, eventNotifier, tacticalMapState, spriteActorFactory, fileImageRetriever, characterImageManager,
                logicalTileTracker,
                boardInputStateProvider,
                contextualAbilityFactory, pulseAnimationGenerator, textLabelGenerator)
    }
}

@Singleton
class TacMapHud(viewPort: Viewport,
                val eventNotifier: EventNotifier,
                val tacticalMapState: TacticalMapState,
                val spriteActorFactory: SpriteActorFactory,
                val fileImageRetriever: FileImageRetriever,
                val characterImageManager: CharacterImageManager,
                val logicalTileTracker: LogicalTileTracker,
                val boardInputStateProvider: BoardInputStateProvider,
                val contextualAbilityFactory: ContextualAbilityFactory,
                val pulseAnimationGenerator: PulseAnimationGenerator,
                val textLabelGenerator: TextLabelGenerator) : Stage(viewPort), EventListener {
    var selectedCharacter: LogicalCharacter? = null
    var hoveredAbility: LogicalAbility? = null
    var entitySelected: TileEntity? = null

    override fun consumeGuiEvent(event: TacticalGuiEvent) {
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
                regenerateTable()
            }
            is TacticalGuiEvent.TileClicked -> {
                val tileEntities = logicalTileTracker.getEntitiesAtTile(event.tileLocation)
                entitySelected = tileEntities.firstOrNull() // TODO:  Not great
                regenerateTable()
            }
            is TacticalGuiEvent.PlayerIsPlacingUnit -> {
                val template = event.unit
                this.selectedCharacter = createFakeLogicalCharacter(template)

                regenerateTable()
                //kludge: This is NOT a valid logicalCharacter because its actor isn't
                // present on the map.
            }
        }
    }

    private fun createFakeLogicalCharacter(template: TacMapUnitTemplate): LogicalCharacter {
        return LogicalCharacter(
                actor = LogicalCharacterActorGroup(template.tiledTexturePath.toActor()),
        tileLocation = TileLocation(0,0),
        tacMapUnit = template,
        playerControlled = true)
    }

    private val buttonDimensions = Dimensions(55, 55)
    private val iconDimensions = Dimensions(55,55)

    private fun actionButton(abilityEquipmentPair : LogicalAbilityAndEquipment): Actor? {

        var actor = abilityEquipmentPair.ability.attackSprite?.toActor()
        if (actor == null){
            actor = ImageIcon(ImageIcon.PAINTERLY_FOLDER, "rip-acid-1.png").toActor() //todo
        }
        val button = actor

        val clickListener = object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.ClickedButtonToActivateAbility(abilityEquipmentPair))
                super.clicked(event, x, y)
            }

            override fun enter(event: InputEvent?, x: Float, y:Float, pointer: Int, fromActor: Actor?){
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.StartedHoveringOverAbility(abilityEquipmentPair))
                hoveredAbility = abilityEquipmentPair.ability
                abilityTextArea.setText("${abilityEquipmentPair.ability.name}: ${abilityEquipmentPair.ability.description}")
                super.enter(event, x, y, pointer, fromActor)
            }
            override fun exit(event: InputEvent?, x: Float, y:Float, pointer: Int, fromActor: Actor?){
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.StoppedHoveringOverAbility(abilityEquipmentPair))
                hoveredAbility = null
                abilityTextArea.setText("")
                super.exit(event, x, y, pointer, fromActor)
            }
        }
        button.actor.addListener(clickListener)
        button.addTooltip(RenderingFunction.simple(abilityEquipmentPair.ability.description?:"INSERT BODY TEXT"))
        return button.actor
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

    var debugTextArea: Label = Label("", DEFAULT_SKIN)
    var abilityTextArea: Label = Label("", DEFAULT_SKIN)
    val characterDisplayTable : Table = Table(DEFAULT_SKIN)
    val characterSelectCarousel : Table = Table(DEFAULT_SKIN)

    fun displayCharacterAttributes(selectedCharacter: LogicalCharacter): Table{
        val table = Table(DEFAULT_SKIN)
        for (item in selectedCharacter.attributes){
            val attrImage = item.imageIcon.toActor(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER.copy(hittable = true))
            attrImage.addTooltip(RenderingFunction.simple(item.description(item)))
            table.add(attrImage.actor).width(iconDimensions.width.toFloat()).height(iconDimensions.height.toFloat())
            val label = Label(item.name, DEFAULT_SKIN)
            label.setWrap(true)
            table.add(label).width(250f)
            table.row()
        }
        return table
    }
    private fun regenerateTable(){
        abilityTextArea = Label("", DEFAULT_SKIN)
        val backgroundColor = backgroundColor()
        characterDisplayTable.setBackground(backgroundColor)
        var selectedCharacter: LogicalCharacter? = selectedCharacter
        regenerateCharacterSelectionCarousel()
        regenerateCharacterDisplayTable(selectedCharacter)
        regenerateTacMapHudCombatPhaseLabel()

        debugTextArea.setText(debugTextAreaText())
        debugTextArea.setWrap(true)
        debugTextArea.setAlignment(Alignment.CENTER.alignment)

        characterDisplayTable.row()
        characterDisplayTable.add(endTurnButton())
        characterDisplayTable.row()
        characterDisplayTable.add(abilityTextArea).width(300f)

        characterDisplayTable.row()
        characterDisplayTable.add(debugTextArea).width(300f)
    }

    val combatPhaseLabel = Table()

    private fun regenerateTacMapHudCombatPhaseLabel(){
        combatPhaseLabel.clearChildren()
        var label = ""
        if (boardInputStateProvider.boardInputState is BoardInputState.PlayerIsPlacingUnits){
            label = "Deployment Phase"
        }
        else {
            label = "Combat Phase"
        }
        val titleLabel = textLabelGenerator.generateLabel(label).label
        titleLabel.setFontScale(.3f)
        combatPhaseLabel.add(titleLabel).height(titleLabel.height)
                .row()

    }

    private fun regenerateCharacterSelectionCarousel() {
        // TODO:  Add indicator saying what the user should be doing here
        characterSelectCarousel.clearChildren()
        val boardInputState = boardInputStateProvider.boardInputState as? BoardInputState.PlayerIsPlacingUnits ?: return
        for (unit in boardInputState.unitsToPlace){
            var nextCharacterActor = characterImageManager.retrieveCharacterTemplateImage(unit).actor
            nextCharacterActor.setScale(3f)
            characterSelectCarousel.add(nextCharacterActor).width(nextCharacterActor.width).pad(10f)
            if (boardInputState.nextUnit()!!.uuid == unit.uuid){
                nextCharacterActor = emphasizeActor(nextCharacterActor)
            }
        }
    }

    private fun emphasizeActor(nextCharacterActor: Actor): Actor {
        nextCharacterActor.addAction(pulseAnimationGenerator.foreverAction())
        return nextCharacterActor
    }

    private fun regenerateCharacterDisplayTable(selectedCharacter: LogicalCharacter?) {
        characterDisplayTable.clearChildren()
        if (selectedCharacter != null) {
            characterDisplayTable.add(Label(selectedCharacter.tacMapUnit.templateName, DEFAULT_SKIN, "title"))
            characterDisplayTable.row()
            characterDisplayTable.add(characterImageManager.retrieveCharacterImage(selectedCharacter).actor)
                    .size(portraitDimensions.width.toFloat(), portraitDimensions.height.toFloat())
            characterDisplayTable.row()
            characterDisplayTable.add(displayCharacterHp(selectedCharacter))
            characterDisplayTable.row()
            characterDisplayTable.add(displayCharacterAttributes(selectedCharacter))
        }
        // NOTE TO FUTURE SELF: Table controls size of images, DOES NOT RESPECT image preferred size

        addActionButtons(selectedCharacter)
        characterDisplayTable.add(Label("", DEFAULT_SKIN)).fillY().expandY()

        val entitySelected = entitySelected
        if (entitySelected != null) {
            characterDisplayTable.add(Label(entitySelected.name, DEFAULT_SKIN))
        }
    }

    private fun addActionButtons(selectedCharacter: LogicalCharacter?) {
        characterDisplayTable.row()
        val abilityTable = Table()
        if (selectedCharacter != null) {
            for (ability in selectedCharacter.abilities) {
                abilityTable.add(actionButton(ability)).width(50f).height(50f)
                abilityTable.add(Label(ability.ability.name, DEFAULT_SKIN))
                abilityTable.row()
            }
            for (ability in contextualAbilityFactory.getContextualAbilitiesAvailableForCharacter(selectedCharacter)) {
                abilityTable.add(actionButton(LogicalAbilityAndEquipment(ability, null))).width(50f).height(50f)
                abilityTable.add(Label(ability.name, DEFAULT_SKIN))
                abilityTable.row()
            }
        }

        characterDisplayTable.add(abilityTable)
    }

    private fun displayCharacterHp(selectedCharacter: LogicalCharacter): Label {
        return Label("HP: ${selectedCharacter.healthLeft}/${selectedCharacter.maxHealth}", DEFAULT_SKIN)
    }

    private fun debugTextAreaText(): String {
        return boardInputStateProvider.boardInputState.name
    }

    private fun backgroundColor(): BackgroundColor {
        val backgroundColor = BackgroundColor("simple/white_color.png")
        backgroundColor.setColor(0, 0, 0, 166)
        return backgroundColor
    }

    init {
        eventNotifier.registerGuiListener(this)

        // this.isDebugAll = true


        val actor =
                Window("", DEFAULT_SKIN).let {
                    it.width = 440f
                    it.height = 600f

                    it.add(combatPhaseLabel).fill().expand()
                    it.row()
                    it.add(characterSelectCarousel).fill().expand()
                    it.row()
                    it.add(characterDisplayTable).fill().expand()

                    it
                }
        window = actor
        val background = Image(DEFAULT_SKIN, "bg")
        background.setScaling(Scaling.stretch)
        background.setFillParent(true)
        window.addActor(background)
        background.toBack()

        actor.x = 650f
        actor.y = 500f


        this.addActor(actor)
    }

    private fun missionObjectivesLabel(): Label {
        val label = Label("Mission Objectives", DEFAULT_SKIN)
        return label
    }
    private fun selectedUnitDescription(): Label {
        val label = Label("", DEFAULT_SKIN)
        label.setWrap(true)
        selectedUnitDescription = label
        return label
    }
    private fun endTurnButton() : Button {
        val button = TextButton("End Turn", DEFAULT_SKIN)
        val clickListener = object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventNotifier.notifyListenersOfGuiEvent((TacticalGuiEvent.EndTurnButtonClicked()))
                super.clicked(event, x, y)
            }
        }
        button.addListener(clickListener)
        return button
    }



}
