package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.extensions.toImage
import com.ironlordbyron.turnbasedstrategy.common.extensions.toNonHittableImage
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.toLibgdxCoordinates
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import com.ironlordbyron.turnbasedstrategy.view.images.fromFileToTextureRegion
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
        actorGroup.addActor(label.label)
        actorGroup.isVisible = false
        return actorGroup
    }
}