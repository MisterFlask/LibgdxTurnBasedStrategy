package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.ui.external.BackgroundColor

public class TacMapTopStatusDisplay(val viewPort: Viewport) : Stage(viewPort), EventListener {

    val overallTable = Table()
    val labelGenerator: TextLabelGenerator by lazy {
        GameModuleInjector.generateInstance(TextLabelGenerator::class.java)
    }


    init{
        this.addActor(overallTable)
        regenerateTable()
    }


    private fun backgroundColor(): BackgroundColor {
        val backgroundColor = BackgroundColor("simple/white_color.png")
        backgroundColor.setColor(0, 0, 0, 166)
        return backgroundColor
    }

    fun regenerateTable(){
        overallTable.setRelativeHeight(1/5f)
        overallTable.clampToTop(1/4f)
        val backgroundColor = backgroundColor()
        overallTable.setBackground(backgroundColor)
        overallTable.debug = true

        overallTable.clear()
        overallTable.add(labelGenerator.generateLabel("Test Label", scale = .2f).label)
    }
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

private fun Float?.shouldBeFractional() {
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
