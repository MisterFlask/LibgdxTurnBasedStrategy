package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.extensions.toImage
import com.ironlordbyron.turnbasedstrategy.common.extensions.toNonHittableImage
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.toLibgdxCoordinates
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import com.ironlordbyron.turnbasedstrategy.view.images.fromFileToTextureRegion
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import com.ironlordbyron.turnbasedstrategy.view.ui.shouldBeFractional
import java.time.Duration

public class SpeechBubbleAnimation{
    val rightwardSpeechBubble = "dialog_ext/speechBubble2.png".fromFileToTextureRegion()

    val top = 2/3f
    val bottom = 1/3f
    val left = 1/4f
    val right = 3/4f

    val textLabelGenerator : TextLabelGenerator by lazy{
        GameModuleInjector.generateInstance(TextLabelGenerator::class.java)
    }

    val GROUP_HEIGHT_OFFSET = 20f

    fun createLocalizedTextBoxWithCharacter(text: String, protoActor: ProtoActor? = null) : Actor{
        val dimensions = ActorDimensions(.2f, .8f, .9f, .7f)
        val textButton=  TextArea(text, DEFAULT_SKIN) // TODO
        return textButton
    }
    val textButtonUnderlay by lazy { "simple/white_color.png".fromFileToTextureRegion() }

    fun createTextBoxAtTopOfScreenWithCharacter(text: String, protoActor: ProtoActor? = null, startVisible: Boolean = false) : Actor {
        val dimensions = ActorDimensions(.2f, .8f, .8f, .6f)

        val textButton = TextArea(text, DEFAULT_SKIN)
        val img = Image(textButtonUnderlay)
        img.color = Color.BLACK
        val actorGroup = Group()
        actorGroup.addActor(img)
        actorGroup.addActor(textButton)
        actorGroup.clampToScreenRatio(dimensions)

        if (!startVisible) {
            //actorGroup.setTrueVisibility(false)
        }

        return actorGroup
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

    fun createSpeechBubbleFromTile(text: String, tileLocation: TileLocation, sideLength: Float = 100f) : Actor {
        val image = rightwardSpeechBubble.toNonHittableImage()
        image.height = sideLength
        image.width = sideLength
        val libgdxCoords = tileLocation.toLibgdxCoordinates()
        image.x = 0f
        image.y = 0f

        val textDimensions = Dimensions(
                width = ((right - left) * image.width).toInt(),
                height = ((top - bottom) * image.height).toInt())
        val label = textLabelGenerator.generateLabel(text, textDimensions, hittable = false, scale = .2f)
        label.label.x = image.width * left
        label.label.y = image.height * bottom
        val actorGroup = Group()
        actorGroup.x = libgdxCoords.x.toFloat()
        actorGroup.y = libgdxCoords.y.toFloat() - GROUP_HEIGHT_OFFSET
        actorGroup.addActor(image)
        //image.width = label.label.width
        //image.height = label.label.height
        actorGroup.addActor(label.label)
        actorGroup.isVisible = false
        return actorGroup
    }
}
