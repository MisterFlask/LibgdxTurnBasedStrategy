package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.graphics.Color
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.external.FloatingText
import com.ironlordbyron.turnbasedstrategy.tiledutils.StageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimationSpeedManager
import javax.inject.Inject

public class FloatingTextGenerator @Inject constructor (val tileMapProvider: TileMapProvider,
                                                       val tiledMapStageProvider: StageProvider
){
    public fun getTemporaryAnimationActorActionPair(text: String, tileLocation: TileLocation, scale: Float = 1.0f): ActorActionPair {

        val floatingText = FloatingText(text, 1000L, .1f * scale)
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        floatingText.setPosition(boundingBox.x.toFloat(), boundingBox.y.toFloat() + boundingBox.height)
        floatingText.setDeltaY(10f)
        floatingText.width = boundingBox.width.toFloat() * scale
        floatingText.height = boundingBox.height.toFloat() * scale
        floatingText.color = Color.RED
        floatingText.setScale(scale)
        tiledMapStageProvider.tiledMapStage.addActor(floatingText)
        return ActorActionPair(floatingText,
                ActorAppearTemporarily(floatingText,
                        durationSeconds = floatingText.animationDurationInMillis.toFloat() / 1000 / AnimationSpeedManager.animationSpeedScale),
                murderActorsOnceCompletedAnimation = true,
                name = "FloatingText")
    }
}