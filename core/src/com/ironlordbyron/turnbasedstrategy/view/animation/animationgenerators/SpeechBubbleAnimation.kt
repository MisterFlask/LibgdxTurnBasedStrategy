package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.toLibgdxCoordinates
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.images.fromFileToTextureRegion
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import com.ironlordbyron.turnbasedstrategy.view.ui.shouldBeFractional

public class SpeechBubbleAnimation{
    val textLabelGenerator by lazy{
        GameModuleInjector.generateInstance(TextLabelGenerator::class.java)
    }

    val GROUP_HEIGHT_OFFSET = 20f

    val textButtonUnderlay by lazy { "simple/white_color.png".fromFileToTextureRegion() }

    fun createLocationOrientedTextBox(text: String,
                                protoActor: ProtoActor? = null,
                                tileLocation: TileLocation): Group {

        val width = 200f
        val height = 80f

        val textButton = TextArea(text, DEFAULT_SKIN)
        val img = Image(textButtonUnderlay)
        img.color = Color.BLACK
        img.width = width
        img.height = height
        textButton.width = width
        textButton.height = height
        val actorGroup = Group()
        actorGroup.addActor(img)
        actorGroup.addActor(textButton)

        val screencoords = tileLocation.toLibgdxCoordinates()
        actorGroup.x = screencoords.x.toFloat() + 30f
        actorGroup.y = screencoords.y.toFloat() + 30f
        return actorGroup
    }

    fun createTextBoxAtTopOfScreenWithCharacter(text: String,
                                                protoActor: ProtoActor) : Actor {
        val dimensions = ActorDimensions(.4f, .8f, .8f, .6f)
        val leftPortraitDimensions = ActorDimensions(.25f, .4f, .8f, .6f)
        val textButton = TextArea(text, DEFAULT_SKIN)
        val img = Image(textButtonUnderlay)
        img.color = Color.BLACK
        val textActorGroup = Group()
        textActorGroup.addActor(img)
        textActorGroup.addActor(textButton)
        textActorGroup.clampToScreenRatio(dimensions)

        val imageActorGroup = Group()
        val portrait = protoActor!!.toActor()
        val portraitBackground = Image(textButtonUnderlay)
        portraitBackground.color = Color.BLACK
        imageActorGroup.addActor(portraitBackground)
        imageActorGroup.addActor(portrait.actor)
        imageActorGroup.clampToScreenRatio(leftPortraitDimensions)

        val totalGroup = Group()
        totalGroup.addActor(imageActorGroup)
        totalGroup.addActor(textActorGroup)
        return totalGroup
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
}
