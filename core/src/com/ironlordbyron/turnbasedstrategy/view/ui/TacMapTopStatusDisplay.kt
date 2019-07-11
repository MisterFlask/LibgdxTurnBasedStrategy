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
        overallTable.setRelativeHeight(1/5f)
        overallTable.clampToTop()

        regenerateTable()
    }


    private fun backgroundColor(): BackgroundColor {
        val backgroundColor = BackgroundColor("simple/white_color.png")
        backgroundColor.setColor(0, 0, 0, 166)
        return backgroundColor
    }

    fun regenerateTable(){
        val backgroundColor = backgroundColor()
        overallTable.setBackground(backgroundColor)

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

fun Actor.clampToTop(){
    val screenwidth  = Gdx.graphics.width.toFloat()
    val screenheight  = Gdx.graphics.height.toFloat()
    this.y = screenheight- this.height
    this.width = screenwidth
    this.x=0f

}

fun Actor.setRelativeHeight(ratio: Float){
    if (ratio > 1){
        throw IllegalArgumentException("Relative height must be between 0 and 1")
    }

    val screenheight  = Gdx.graphics.height.toFloat()
    this.height = screenheight * ratio
}
