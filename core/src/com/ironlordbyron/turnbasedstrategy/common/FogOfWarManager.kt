package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.graphics.Color
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tacmapunits.actionManager
import com.ironlordbyron.turnbasedstrategy.tiledutils.FogStatus
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.fogStatus
import com.ironlordbyron.turnbasedstrategy.tiledutils.getPlayerPlacementTiles
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WallEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.FogOfWarAlphaAnimationGenerator

public data class FogOfWarVisibilityClump(val tiles: Collection<TileLocation>, val name: String? = null)

public class FogOfWarManager{
    val tacticalMapAlgorithms by LazyInject(TacticalMapAlgorithms::class.java)
    val tacMapState by LazyInject(TacticalMapState::class.java)
    val logicalTileTracker by LazyInject(LogicalTileTracker::class.java)
    val visibilityClumps = ArrayList<FogOfWarVisibilityClump>()

    val everVisibleTiles = HashSet<TileLocation>()

    public fun markTilesAsVisible(clumpName: String, tiles: List<TileLocation>){
        visibilityClumps.add(FogOfWarVisibilityClump(tiles, clumpName))
    }
    public fun removeVisibilityClump(clumpName: String){
        visibilityClumps.removeIf{it.name == clumpName}
    }

    public fun getVisionForUnit(logicalCharacter: LogicalCharacter) : Collection<TileLocation>{
        var iteration = 9
        val tilesVisible = HashSet<TileLocation>()
        var nextSetOfTiles = HashSet<TileLocation>()
        var tilesToBeProcessed = hashSetOf(logicalCharacter.tileLocation)
        while(tilesToBeProcessed.isNotEmpty() && iteration > 0){
            val nextTile = tilesToBeProcessed.first()
            tilesToBeProcessed.remove(nextTile)
            val neighbors = nextTile.neighbors()
                    .filter{canViewPastTile(it)}
            nextSetOfTiles.addAll(neighbors.filter{tilesVisible.doesNotContain(it)})
            tilesVisible.add(nextTile)
            if (tilesToBeProcessed.isEmpty()){
                tilesToBeProcessed.addAll(nextSetOfTiles)
                iteration--
            }
        }

        val justNeighbors = tilesToBeProcessed.flatMap{it.neighbors()} - tilesVisible
        val tilesJustBarelyVisible = justNeighbors.filter{canViewSlightlyPastVisionRadius(it)}
        return tilesVisible + tilesJustBarelyVisible
    }

    public fun canViewSlightlyPastVisionRadius(tileLocation: TileLocation): Boolean {
        val entity = tileLocation.entity()
        if (entity == null) return false
        return entity is DoorEntity || entity is WallEntity
    }

    public fun canViewPastTile(tileLocation: TileLocation) : Boolean{
        val entity = tileLocation.entity()
        if (entity == null) return true
        if (entity is WallEntity){
            return false
        }
        if (entity is DoorEntity && !entity.isOpen){
            return false
        }
        return true
    }

    public fun getVisionForPlayer(): Set<TileLocation> {
        val playerCharacters = tacMapState.listOfPlayerCharacters
        val visibleTileLocations = HashSet<TileLocation>()
        for(character in playerCharacters){
            visibleTileLocations.addAll(getVisionForUnit(character))
        }
        // now, turn all organs visible

        tacMapState.listOfCharacters
                .filter{it.tacMapUnit.tags.isOrgan}
                .map{it.tileLocation}
                .forEach{visibleTileLocations.add(it)}

        // now, all player spawn positions

        val characterSpawningZones = tiledMapProvider.tiledMap.getPlayerPlacementTiles()
                .expandByRadius(1)
        for (tile in characterSpawningZones){
            visibleTileLocations.add(tile)
        }

        val otherVisibleTilesFromClumps = this.visibilityClumps.flatMap { it.tiles }
        return visibleTileLocations + otherVisibleTilesFromClumps
    }

    val UNDER_FOG_OF_WAR_COLOR = Color.GRAY!!

    public fun updateVisuals(){
        val visibleTiles = getVisionForPlayer()
        everVisibleTiles.addAll(visibleTiles)
        actionManager.animateFogOfWarShift(visibleTiles)
        actionManager.runThroughActionQueue()
    }

    public fun setStartingFogOfWar(){
        val allTiles = logicalTileTracker.tiles
        for (tile in allTiles.values){
            tile.underFogOfWar = FogStatus.BLACK
        }

        updateVisuals()
    }


}