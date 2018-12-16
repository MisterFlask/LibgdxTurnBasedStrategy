package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.images.FileImageRetriever
import com.ironlordbyron.turnbasedstrategy.view.images.Icon
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import javax.inject.Inject

class TargetingCursorManager @Inject constructor(val tiledMapStageProvider: TacticalTiledMapStageProvider,
                                                 val imageRetriever: FileImageRetriever,
                                                 val eventNotifier: EventNotifier) : EventListener{
    override fun consumeEvent(event: TacticalGuiEvent){
        when(event){
            is TacticalGuiEvent.TileHovered -> hoversOverNewTile(event.location)
        }
    }

    init{
        eventNotifier.registerListener(this)
    }

    var currentCursor: Actor? = null

    fun hoversOverNewTile(tileLocation: TileLocation){
        currentCursor?.remove()
        currentCursor =imageRetriever.retrieveIconImageAsActor(Icon.TARGETING_CURSOR, tileLocation)
        tiledMapStageProvider.tiledMapStage.addActor(currentCursor)
    }
}