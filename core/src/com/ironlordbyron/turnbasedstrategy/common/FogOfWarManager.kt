package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.graphics.Color
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.FogStatus
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.fogStatus
import com.ironlordbyron.turnbasedstrategy.view.animation.SpriteColorActorAction


public class FogOfWarManager{
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
            if (isVisible) {
                it.underFogOfWar = FogStatus.VISIBLE
            } else {
                if (it.underFogOfWar == FogStatus.VISIBLE){
                    it.underFogOfWar = FogStatus.NOT_VISIBLE
                }
            }
            it.fogOfWarTileActor.color.a = it.underFogOfWar.fogAlpha
        }

        // now, we turn all tile entities invisible that are under fog of war.
        tacMapState.listOfCharacters
                .filter{!it.tacMapUnit.tags.isOrgan}
                .filter{it.tileLocation.fogStatus() != FogStatus.VISIBLE}
                .forEach { it.actor.isVisible = false }

        tacMapState.listOfCharacters
                .filter{it.tileLocation.fogStatus() == FogStatus.VISIBLE}
                .forEach { it.actor.isVisible = true }

        // now, turn all organs visible

        tacMapState.listOfCharacters
                .filter{it.tacMapUnit.tags.isOrgan}
                .map{it.tileLocation}
                .map{logicalTileTracker.tiles[it]}
                .forEach{it!!.underFogOfWar = FogStatus.NOT_VISIBLE}
    }

    public fun setStartingFogOfWar(){
        val allTiles = logicalTileTracker.tiles
        for (tile in allTiles.values){
            tile.underFogOfWar = FogStatus.BLACK
        }
        val characterSpawningZones = tiledMapInterpreter.getPossiblePlayerSpawnPositions(tiledMapProvider.tiledMap)
        for (tile in characterSpawningZones){
            val logTile = logicalTileTracker.tiles[tile]
            logTile!!.underFogOfWar = FogStatus.VISIBLE
        }

        updateVisuals()
    }


}