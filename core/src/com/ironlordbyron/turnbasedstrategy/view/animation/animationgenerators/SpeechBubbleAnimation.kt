package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.toLibgdxCoordinates
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.images.fromFileToTextureRegion
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import com.ironlordbyron.turnbasedstrategy.view.ui.shouldBeFractional
import com.ironlordbyron.turnbasedstrategy.view.ui.withBorder

public class SpeechBubbleAnimation{
    val textLabelGenerator by lazy{
        GameModuleInjector.generateInstance(TextLabelGenerator::class.java)
    }

    val GROUP_HEIGHT_OFFSET = 20f

    val textButtonUnderlay by lazy { "simple/white_color.png".fromFileToTextureRegion() }

    fun createLocationOrientedTextBox(text: String,
                                tileLocation: TileLocation): Group {

        val width = 300f
        val height = 150f
        val textButton = TextArea(text, DEFAULT_SKIN)
        val table = Table()
        table.width = width
        table.height = height
        table.add(textButton).fill().expand()
        table.withBorder()

        val screencoords = tileLocation.toLibgdxCoordinates()
        table.x = screencoords.x.toFloat() + 30f
        table.y = screencoords.y.toFloat() + 30f
        return table
    }

    fun createTextBoxAtTopOfScreenWithCharacter(text: String,
                                                protoActor: ProtoActor) : Actor {
        val dimensions = ActorDimensions(.25f, .8f, .2f, 0f)
        val textButton = TextArea(text, DEFAULT_SKIN)
        val table = Table()
        table.clampToScreenRatio(dimensions)
        val actor = protoActor.toActorWrapper().actor
        val aspectRatio = actor.height / actor.width

        table.add(actor).height(table.height).width(table.height / aspectRatio)
        table.add(textButton).expand().fill()
        return table.withBorder(scale = 3f)
    }

}


data class ActorDimensions(val leftBorder: Float, val rightBorder: Float, val topBorder: Float, val botBorder: Float){
    init{
        leftBorder.shouldBeFractional()
        rightBorder.shouldBeFractional()
        topBorder.shouldBeFractional()
        botBorder.shouldBeFractional()
        assert(leftBorder < rightBorder)
        assert(topBorder > botBorder)
    }
}
fun Actor.clampToScreenRatio(dimensions: ActorDimensions){
    val screenwidth  = Gdx.graphics.width.toFloat()
    val screenheight  = Gdx.graphics.height.toFloat()

    this.width = screenwidth * (dimensions.rightBorder - dimensions.leftBorder)
    this.height = screenheight * (dimensions.topBorder - dimensions.botBorder)
    this.x = screenwidth * dimensions.leftBorder
    this.y = screenheight * dimensions.topBorder - this.height
    if (this is Group){
        for (actor in this.children){
            actor.width = this.width
            actor.height = this.height
        }
    }
}