package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
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
import com.ironlordbyron.turnbasedstrategy.common.wrappers.addSimpleTooltip
import com.ironlordbyron.turnbasedstrategy.controller.*
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.StageProvider
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

public val DEFAULT_SKIN: Skin = Skin(Gdx.files.internal("tactical-ui-skins/shade_skin/shadeui/uiskin.json"))

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
    val stageProvider: StageProvider by LazyInject(StageProvider::class.java)
    fun create(viewPort: Viewport): TacMapHud {
        val tmh = TacMapHud(viewPort, eventNotifier, tacticalMapState, spriteActorFactory, fileImageRetriever, characterImageManager,
                logicalTileTracker,
                boardInputStateProvider,
                contextualAbilityFactory, pulseAnimationGenerator, textLabelGenerator)
        stageProvider.tacMapHudStage = tmh
        return tmh
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
    var tileSelected: TileLocation? = null


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
                tileSelected = event.tileLocation
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
                actor = LogicalCharacterActorGroup(template.tiledTexturePath.toActorWrapper()),
        tileLocation = TileLocation(0,0),
        tacMapUnit = template,
        playerControlled = true)
    }

    private val buttonDimensions = Dimensions(55, 55)
    private val iconDimensions = Dimensions(55,55)

    private fun actionButton(abilityEquipmentPair : LogicalAbilityAndEquipment): Actor? {

        var actor = abilityEquipmentPair.ability.attackSprite?.toActorWrapper()
        if (actor == null){
            actor = ImageIcon(ImageIcon._PAINTERLY_FOLDER, "rip-acid-1.png").toActorWrapper() //todo
        }
        val button = actor
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
    val nonUnitActionsDisplay : Table = Table(DEFAULT_SKIN)

    fun displayCharacterAttributes(selectedCharacter: LogicalCharacter): Table{
        val table = Table(DEFAULT_SKIN)
        for (item in selectedCharacter.getAttributes()){
            val attrImage = item.logicalAttribute.imageIcon.toActorWrapper(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER.copy(hittable = true))
            attrImage.addTooltip(RenderingFunction.simple(item.logicalAttribute.description(item.stacks)))
            table.add(attrImage.actor).width(iconDimensions.width.toFloat()).height(iconDimensions.height.toFloat())
            if (item.stacks > 1) {
                table.add(Label("[${item.stacks.toString()}]", DEFAULT_SKIN))
            }
            val label = Label(item.logicalAttribute.name, DEFAULT_SKIN)
            label.setWrap(true)
            table.add(label)
            table.row()
        }
        return table
    }
    private fun regenerateTable(){
        // characterDisplayTable.debug()
        abilityTextArea = Label("", DEFAULT_SKIN)
        val backgroundColor = backgroundColor()
        characterDisplayTable.setBackground(backgroundColor)
        var selectedCharacter: LogicalCharacter? = selectedCharacter
        regenerateCharacterSelectionCarousel()
        regenerateCharacterDisplayTable(selectedCharacter)
        regenerateTacMapHudCombatPhaseLabel()

        debugTextArea.setText(debugTextAreaText())
        debugTextArea.setWrap(true)
        debugTextArea.setAlignment(Alignment.LEFT.alignment)

        characterDisplayTable.row()
        characterDisplayTable.add(endTurnButton())
        characterDisplayTable.row()
        characterDisplayTable.add(abilityTextArea).width(300f)

        characterDisplayTable.row()

        val entitySelected = entitySelected
        if (entitySelected != null) {
            val entityTable = entitySelected.buildUiDisplay().withBorder()
            entityTable.debugTable()
            characterDisplayTable.add(entityTable).height(100f).left()
            characterDisplayTable.row()
        }


        characterDisplayTable.add(debugTextArea).width(300f)
        characterDisplayTable.withBorder()
    }

    val combatPhaseLabel = Table()

    private fun regenerateTacMapHudCombatPhaseLabel(){
        combatPhaseLabel.debugTable()
        combatPhaseLabel.clearChildren()
        var label: String
        if (boardInputStateProvider.boardInputState is BoardInputState.PlayerIsPlacingUnits){
            label = "Deployment Phase"
        }
        else {
            label = "Combat Phase"
        }
        val titleLabel = textLabelGenerator.generateSkinnedLabel(label).label
        titleLabel.setFontScale(1.2f)
        combatPhaseLabel.add(titleLabel).height(titleLabel.height).expand().fill()
                .row()
    }

    private fun regenerateCharacterSelectionCarousel() {
        // TODO:  Add indicator saying what the user should be doing here
        characterSelectCarousel.clearChildren()
        val boardInputState = boardInputStateProvider.boardInputState as? BoardInputState.PlayerIsPlacingUnits ?: return
        for (unit in tacMapState.unitsAvailableToDeploy){
            var nextCharacterActor = characterImageManager.retrieveCharacterTemplateImage(unit).actor
            nextCharacterActor.setScale(3f)
            characterSelectCarousel.add(nextCharacterActor).width(nextCharacterActor.width).pad(10f)
            if (boardInputState.nextUnit()!!.unitId == unit.unitId){
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
            characterDisplayTable.align(Alignment.LEFT.alignment)
            characterDisplayTable.add(Label(selectedCharacter.tacMapUnit.templateName, DEFAULT_SKIN, "title"))
            characterDisplayTable.row()
            characterDisplayTable.add(characterImageManager.retrieveCharacterImage(selectedCharacter).actor)
                    .size(portraitDimensions.width.toFloat(), portraitDimensions.height.toFloat())
            characterDisplayTable.row()
            characterDisplayTable.add(displayCharacterHp(selectedCharacter))
            characterDisplayTable.row()
            characterDisplayTable.add(displayCharacterAttributes(selectedCharacter)).left()
            characterDisplayTable.row()
            val turnStartAction = selectedCharacter.tacMapUnit.turnStartAction
            if (turnStartAction != null){
                characterDisplayTable.addLabel("AT TURN START")
                characterDisplayTable.row()
                characterDisplayTable.addLabel(turnStartAction.displayName, tooltip = turnStartAction.extendedDescription)
                characterDisplayTable.row()
                characterDisplayTable.addLabel("[" + turnStartAction.cooldownDescription + "]")
            }
        }
        // NOTE TO FUTURE SELF: Table controls size of images, DOES NOT RESPECT image preferred size

        addActionButtons(selectedCharacter)


    }

    private fun addActionButtons(selectedCharacter: LogicalCharacter?) {
        characterDisplayTable.row()
        val abilityTable = Table()
        val abilityScroller = ScrollPane(abilityTable)
        abilityScroller.setFadeScrollBars(false)
        abilityScroller.setScrollbarsOnTop(true)

        if (selectedCharacter != null) {
            for (ability in selectedCharacter.abilities) {
                abilityTable.add(createActionButtonForAbility(ability)).width(250f).height(70f)
                abilityTable.row()
            }
            for (ability in contextualAbilityFactory.getContextualAbilitiesAvailableForCharacter(selectedCharacter)) {
                abilityTable.add(createActionButtonForAbility(LogicalAbilityAndEquipment(ability, null))).width(250f).height(70f)
                abilityTable.row()
            }
        }

        characterDisplayTable.add(abilityScroller)
    }

    private fun createActionButtonForAbility(abilityAndEquipment: LogicalAbilityAndEquipment) : Table{
        val singleAbilityTable = Table()
        singleAbilityTable.add(actionButton(abilityAndEquipment))
                .width(50f).height(50f)
        singleAbilityTable.add(Label(abilityAndEquipment.ability.name, DEFAULT_SKIN))
        singleAbilityTable.withBorder()

        val clickListener = object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.ClickedButtonToActivateAbility(abilityAndEquipment))
                super.clicked(event, x, y)
            }

            override fun enter(event: InputEvent?, x: Float, y:Float, pointer: Int, fromActor: Actor?){
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.StartedHoveringOverAbility(abilityAndEquipment))
                hoveredAbility = abilityAndEquipment.ability
                abilityTextArea.setText("${abilityAndEquipment.ability.name}: ${abilityAndEquipment.ability.description}")
                super.enter(event, x, y, pointer, fromActor)
            }
            override fun exit(event: InputEvent?, x: Float, y:Float, pointer: Int, fromActor: Actor?){
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.StoppedHoveringOverAbility(abilityAndEquipment))
                hoveredAbility = null
                abilityTextArea.setText("")
                super.exit(event, x, y, pointer, fromActor)
            }
        }
        singleAbilityTable.touchable = Touchable.enabled
        singleAbilityTable.addListener(clickListener)
        singleAbilityTable.addSimpleTooltip(abilityAndEquipment.ability.description?:"INSERT BODY TEXT")

        return singleAbilityTable
    }

    private fun displayCharacterHp(selectedCharacter: LogicalCharacter): Label {
        return Label("HP: ${selectedCharacter.healthLeft}/${selectedCharacter.maxHealth}", DEFAULT_SKIN)
    }

    private fun debugTextAreaText(): String {
        if (selectedCharacter != null){
            return (boardInputStateProvider.boardInputState.name
                    + "\n" + this.selectedCharacter?.tileLocation
                    + "\n" + this.selectedCharacter?.intent
                    + "\n" + this.selectedCharacter?.tacMapUnit?.metagoal)
        } else{
            return tileSelected?.terrainType().toString()
        }
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

                    it.add(combatPhaseLabel.withBorder()).fill().expand().fill()
                    it.row()
                    it.add(characterSelectCarousel.withBorder()).fill().expand()
                    it.row()
                    it.add(characterDisplayTable).fill().expand()

                    it
                }
        window = actor

        actor.setRelativeWidth(1/4f)
        actor.clampToRightSide()

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

/*
private fun <T: Actor> Cell<T>.surroundWithBorder(): Cell<T> {
    if (this.actor?.stage == null) return this
    val borderActor = PainterlyBorders.blueFrame.toActorWrapper()
    borderActor.actor.width = this.actor.width
    borderActor.actor.height = this.actor.height
    borderActor.actor.x = this.actor.x
    borderActor.actor.y = this.actor.y
    this.actor.stage.addActor(borderActor.actor)
    return this
}
*/
private fun Cell<*>.scaleByDimension(target: Dimensions, scale: Float): Cell<*> {

    return this.width(target.width * scale).height(target.height * scale)
}
