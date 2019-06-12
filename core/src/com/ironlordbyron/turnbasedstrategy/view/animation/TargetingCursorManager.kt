package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.controller.*
import com.ironlordbyron.turnbasedstrategy.view.images.FileImageRetriever
import com.ironlordbyron.turnbasedstrategy.view.images.Icon
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import javax.inject.Inject

class TargetingCursorManager @Inject constructor(val tiledMapStageProvider: TacticalTiledMapStageProvider,
                                                 val imageRetriever: FileImageRetriever,
                                                 val eventNotifier: EventNotifier,
                                                 val stateProvider: BoardInputStateProvider,
                                                 val actionManager: ActionManager,
                                                 val animationActionQueueProvider: AnimationActionQueueProvider) : EventListener{
    override fun consumeGuiEvent(event: TacticalGuiEvent){
        when(event){
            is TacticalGuiEvent.TileHovered -> hoversOverNewTile(event.location)
        }
    }

    init{
        eventNotifier.registerGuiListener(this)
    }

    var currentCursor: Actor? = null

    fun hoversOverNewTile(tileLocation: TileLocation){
        currentCursor?.remove()
        val state = stateProvider.boardInputState
        if (state is BoardInputState.PlayerIsPlacingUnits){
            currentCursor = actionManager.spawnEntityAtTileInSequence(state.nextUnit()!!.tiledTexturePath, tileLocation)
            currentCursor!!.addAction(Actions.alpha(.5f))
            animationActionQueueProvider.runThroughActionQueue()
        }else{
            currentCursor = imageRetriever.retrieveIconImageAsActor(Icon.TARGETING_CURSOR, tileLocation)
            tiledMapStageProvider.tiledMapStage.addActor(currentCursor)
        }
    }
}