package com.ironlordbyron.turnbasedstrategy.view.animation.passive

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.ai.PathfinderFactory
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.StageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.images.FileImageRetriever
import com.ironlordbyron.turnbasedstrategy.view.images.Icon
import javax.inject.Inject

/**
 * This is responsible for showing the proposed path between where the character is and where the character is going.
 */
public class BreadcrumbsPathAnimator @Inject constructor(val tileMapProvider: TileMapProvider,
                                                         val imageRetriever: FileImageRetriever,
                                                         val tiledMapStageProvider: StageProvider,
                                                         val eventNotifier: EventNotifier,
                                                         val boardInputStateProvider: BoardInputStateProvider,
                                                         val pathfinderFactory: PathfinderFactory) : EventListener { // add pathfinder
    init{
        eventNotifier.registerGuiListener(this)
    }

    override fun consumeGuiEvent(event: TacticalGuiEvent){
        when(event){
            is TacticalGuiEvent.SwitchedGuiState -> {
                if (!(event.guiState is BoardInputState.UnitSelected)){
                    animateBreadCrumbs(listOf())
                }
            }
        }

        val boardInputState = boardInputStateProvider.boardInputState
        when(boardInputState){
            is BoardInputState.UnitSelected -> {
                when(event){
                    is TacticalGuiEvent.TileHovered -> {
                        val pathfinder = pathfinderFactory.createGridGraph(boardInputState.unit)
                        val tiles = pathfinder.acquireBestPathTo(
                                boardInputState.unit,
                                event.location,
                                allowEndingOnLastTile = true)
                        if (tiles == null){
                            return
                        }
                        animateBreadCrumbs(tiles.map{it.location})
                    }
                }
            }
        }
    }
    val breadcrumbs = ArrayList<Actor>()
    fun animateBreadCrumbs(tiles: Collection<TileLocation>){
        breadcrumbs.forEach{it.remove()}

        for (tile in tiles){
            val actor = imageRetriever.retrieveIconImageAsActor(Icon.BREADCRUMB, tile)
            tiledMapStageProvider.tiledMapStage.addActor(actor)
            breadcrumbs.add(actor)
        }
    }
}