package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.Viewport
import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.wrappers.LabelWrapper
import com.ironlordbyron.turnbasedstrategy.common.wrappers.RenderingFunction
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject

public class TacMapTopStatusDisplay(val viewPort: Viewport) : Stage(viewPort),
        GameEventListener {
    val eventNotifier: EventNotifier by LazyInject(EventNotifier::class.java)

    init {
        eventNotifier.registerGameListener(this)
    }

    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        regenerateTable()
    }

    val overallTable = Table()
    val labelGenerator: TextLabelGenerator by lazy {
        GameModuleInjector.generateInstance(TextLabelGenerator::class.java)
    }


    init{
        this.addActor(overallTable)
    }


    val tacMapGlobalState: GlobalTacMapState by lazy {
        GameModuleInjector.generateInstance(GlobalTacMapState::class.java)
    }

    fun regenerateTable(){

        overallTable.setRelativeHeight(1/5f)
        overallTable.clampToTop(1/4f)

        overallTable.clear()

        val nextEvent = tacMapGlobalState.nextEvent()

        overallTable.add(buildLabel("Alertness: ${tacMapGlobalState.alertness}")).row()
        overallTable.add(buildLabel("Next event: ${nextEvent.eventName} at ${nextEvent.atAlertness}")).row()
        overallTable.withBorder()

        // Now, the goals!


        val goalTable = Table()
        goalTable.withGoldBorderBlackBackground()
        goalTable.add(Label("OBJECTIVES:", DEFAULT_SKIN)).row()
        for (goal in tacMapGlobalState.battleGoals){
            val goalMetStr = "[${goal.getGoalProgressString()}]"
            val label = Label("*${goal.name}: ${goal.description} $goalMetStr"  , DEFAULT_SKIN)
            if (goal.isGoalMet()){
                label.color = Color.GREEN
            } else {
                label.color = Color.RED
            }
            goalTable.add(label).row()
        }
        overallTable.row()
        overallTable.add(goalTable)
    }
}

val textLabelGenerator: TextLabelGenerator by lazy{
    GameModuleInjector.generateInstance(TextLabelGenerator::class.java)
}

fun buildLabel(str: String, tooltip: String? = null) : Label{
    val label = Label(str, DEFAULT_SKIN)
    if (tooltip != null) label.addTooltipText(tooltip)
    return label
}

fun Actor.addTooltipText(str: String){
    TooltipManager.getInstance().maxWidth = 200f
    val tooltip = TextTooltip(str, DEFAULT_SKIN)
    tooltip.setInstant(true)
    tooltip.setAlways(true)
    this.addListener(tooltip)
}

fun Table.addLabel(text: String, scale: Float= .3f, tooltip: String? = null, skipRow: Boolean = false,
                   afterCreation: (Cell<Label>) -> Unit = {}): LabelWrapper {
    if (!skipRow) this.row()
    val label = textLabelGenerator.generateSkinnedLabel(text,  scale = scale)
    afterCreation(this.add(label.label))
    if (tooltip != null){
        label.addTooltip(RenderingFunction.simple(tooltip))
    }
    return label
}

fun Table.addSubtitleLabel(text: String, scale: Float= .15f, tooltip: String? = null, skipRow: Boolean = false,
                           afterCreation: (Cell<Label>) -> Unit = {}){
    addLabel(text, scale, tooltip, skipRow, afterCreation)
}


fun Actor.clampToRightSide(){
    val screenwidth  = Gdx.graphics.width.toFloat()
    val screenheight  = Gdx.graphics.height.toFloat()
    this.x = screenwidth - this.width
    this.height = screenheight
    this.y=  0f
}

fun Actor.clampToTop(rightExclusion: Float?){
    rightExclusion.shouldBeFractional()
    val screenwidth  = Gdx.graphics.width.toFloat()
    val screenheight  = Gdx.graphics.height.toFloat()
    this.y = screenheight- this.height
    this.width = screenwidth
    this.x=0f
    if (rightExclusion != null){
        // we're excluding the fraction % of the screen corresponding to rightExclusion
        this.width = screenwidth * (1 - rightExclusion)
    }
}

fun Float?.shouldBeFractional() {
    if (this == null){
        throw IllegalArgumentException("Must be a fraction, but was null")
    }
    if (this < 0 || this > 1){
        throw IllegalArgumentException("Must be a fraction, but was $this")
    }
}

fun Actor.setRelativeHeight(ratio: Float){
    if (ratio > 1){
        throw IllegalArgumentException("Relative height must be between 0 and 1")
    }

    val screenheight  = Gdx.graphics.height.toFloat()
    this.height = screenheight * ratio
}

fun Actor.setRelativeWidth(ratio: Float){
    if (ratio > 1){
        throw IllegalArgumentException("Relative height must be between 0 and 1")
    }

    val screenWidth  = Gdx.graphics.width.toFloat()
    this.width = screenWidth * ratio
}
