package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.graphics.Color
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.SpriteColorActorAction


public class VisionManager{
    val tacticalMapAlgorithms by LazyInject(TacticalMapAlgorithms::class.java)
    val tacMapState by LazyInject(TacticalMapState::class.java)
    val logicalTileTracker by LazyInject(LogicalTileTracker::class.java)
    public fun getVisionForPlayer(): HashSet<TileLocation> {
        val playerCharacters = tacMapState.listOfPlayerCharacters
        val visibleTileLocations = HashSet<TileLocation>()
        for(character in playerCharacters){
            visibleTileLocations.addAll(tacticalMapAlgorithms.getTileLocationsUpToNAway(10, character.tileLocation, character))
        }
        return visibleTileLocations
    }

    val UNDER_FOG_OF_WAR_COLOR = Color.GRAY!!

    public fun updateVisuals(){
        val visibleTiles = getVisionForPlayer()
        logicalTileTracker.tiles.values.forEach{
            val isVisible = visibleTiles.contains(it.location)
            // the tiles aren't actually actors, so we just have to create tiles over them.
            it.underFogOfWar = !isVisible
            if (it.underFogOfWar) {
                it.fogOfWarTileActor.color.a = .5f
            } else {
                it.fogOfWarTileActor.color.a = 0f
            }
        }
    }

}