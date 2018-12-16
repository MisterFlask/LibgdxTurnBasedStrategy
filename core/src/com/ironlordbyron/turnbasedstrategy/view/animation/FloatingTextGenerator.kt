package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.Color
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.external.FloatingText
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class FloatingTextGenerator @Inject constructor (val tileMapProvider: TileMapProvider,
                                                       val tiledMapStageProvider: TacticalTiledMapStageProvider
){
    public fun getTemporaryAnimationActorActionPair(text: String, tileLocation: TileLocation): ActorActionPair{

        val floatingText = FloatingText(text, 1000L)
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        floatingText.setPosition(boundingBox.x.toFloat(), boundingBox.y.toFloat() + boundingBox.height)
        floatingText.setDeltaY(10f)
        floatingText.width = boundingBox.width.toFloat()
        floatingText.height = boundingBox.height.toFloat()
        floatingText.color = Color.RED
        tiledMapStageProvider.tiledMapStage.addActor(floatingText)
        return ActorActionPair(floatingText,
                ActorAppearTemporarily(floatingText,
                durationSeconds = floatingText.animationDurationInMillis.toFloat() / 1000),
                murderActorsOnceCompletedAnimation = true,
                name = "FloatingText")
    }
}