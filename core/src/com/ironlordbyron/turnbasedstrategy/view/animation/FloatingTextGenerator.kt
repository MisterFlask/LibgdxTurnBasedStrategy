package com.ironlordbyron.turnbasedstrategy.view.animation

import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.external.FloatingText
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class FloatingTextGenerator @Inject constructor (val tileMapProvider: TileMapProvider,
                                                       val tiledMapStageProvider: TacticalTiledMapStageProvider
){
    public fun getTemporaryAnimationActorActionPair(tileLocation: TileLocation): ActorActionPair{

        val floatingText = FloatingText("SAMPLE TEXT", 10L)
        tiledMapStageProvider.tiledMapStage.addActor(floatingText)
        return ActorActionPair(floatingText,
                ActorAppearTemporarily(floatingText, 1000),
                murderActorsOnceCompletedAnimation = true,
                name = "FloatingText")
    }
}