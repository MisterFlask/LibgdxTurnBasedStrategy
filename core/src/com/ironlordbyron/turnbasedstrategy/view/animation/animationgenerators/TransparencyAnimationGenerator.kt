package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.getCharacter
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
    val revealActionGenerator by LazyInject(RevealActionGenerator::class.java)
    val hideActionGenerator by LazyInject(HideAnimationGenerator::class.java)

    fun buildFogOfWarAlphaChangeActions(fogStatus: FogStatus, tiles: Collection<TileLocation>) : Collection<ActorActionPair>{
        if (tiles.isEmpty()) return ArrayList()
        val pairs = ArrayList<ActorActionPair>()
        for (actor in tiles.map{it.logicalTile()!!.fogOfWarTileActor}){
            pairs.add(ActorActionPair(actor, Actions.alpha(fogStatus.fogAlpha, .4f)))
        }
        return pairs
    }

    fun getVisualTransitionsActorActionPairs(visibleLocations: Collection<TileLocation>, modifyState: Boolean = true) : Collection<ActorActionPair> {
        val locationsToBeRevealed = visibleLocations
        val locationsToHide = logicalTileTracker
                .tiles
                .filter { it.key !in visibleLocations }
                .filter { it.value.underFogOfWar == FogStatus.VISIBLE }
        val characterActions = ArrayList<ActorActionPair>()
        if( modifyState){
            locationsToHide.forEach{it.value.underFogOfWar = FogStatus.NOT_VISIBLE}
            locationsToBeRevealed.map { it.logicalTile()!!}.forEach{it.underFogOfWar = FogStatus.VISIBLE}

            val charactersToReveal = locationsToBeRevealed
                    .filter{it.getCharacter() != null}
                    .map{it.getCharacter()!!}

            val charactersToHide = locationsToHide.keys
                    .filter{it.getCharacter() != null}
                    .map{it.getCharacter()!!}
            val hideActions = charactersToHide.map{hideActionGenerator.generateHideActorActionPair(it.actor)}
            val revealActions = charactersToReveal.map{revealActionGenerator.generateRevealActorActionPair(it.actor)}
            characterActions.addAll(hideActions + revealActions)
        }
        val hideActions = buildFogOfWarAlphaChangeActions(FogStatus.NOT_VISIBLE, locationsToHide.keys)
        val revealActions = buildFogOfWarAlphaChangeActions(FogStatus.VISIBLE, locationsToBeRevealed)
        return hideActions + revealActions + characterActions
    }


}

fun MutableList<ActorActionPair>.toSingleActorActionPair() : ActorActionPair{
    val primaryActor = this.pop()!!
    return ActorActionPair(primaryActor.actor, primaryActor.action, secondaryActions = this, cameraTrigger = false)
}