package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.logicalTile
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.missiongen.pop
import com.ironlordbyron.turnbasedstrategy.tiledutils.FogStatus
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTile
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import java.lang.IllegalArgumentException

public class FogOfWarAlphaAnimationGenerator{

    val logicalTileTracker by LazyInject(LogicalTileTracker::class.java)

    fun buildFogOfWarAlphaChangeActions(fogStatus: FogStatus, tiles: Collection<TileLocation>) : Collection<ActorActionPair>{
        if (tiles.isEmpty()) return ArrayList()
        val pairs = ArrayList<ActorActionPair>()
        for (actor in tiles.map{it.logicalTile()!!.fogOfWarTileActor}){
            pairs.add(ActorActionPair(actor, Actions.alpha(fogStatus.fogAlpha, .4f)))
        }
        return pairs
    }

    fun getVisualTransitionsActorActionPairs(visibleLocations: Collection<TileLocation>) : Collection<ActorActionPair> {
        val locationsToBeRevealed = visibleLocations.filter { it.logicalTile()!!.underFogOfWar != FogStatus.VISIBLE }
        val locationsToHide = logicalTileTracker
                .tiles
                .filter { it.key !in visibleLocations }
                .filter { it.value.underFogOfWar == FogStatus.VISIBLE }
        val hideActions = buildFogOfWarAlphaChangeActions(FogStatus.NOT_VISIBLE, locationsToHide.keys)
        val revealActions = buildFogOfWarAlphaChangeActions(FogStatus.VISIBLE, locationsToBeRevealed)
        return hideActions + revealActions
    }


}

fun MutableList<ActorActionPair>.toSingleActorActionPair() : ActorActionPair{
    val primaryActor = this.pop()!!
    return ActorActionPair(primaryActor.actor, primaryActor.action, secondaryActions = this, cameraTrigger = false)
}