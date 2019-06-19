package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import tiled.core.Tile
import javax.inject.Inject
import javax.inject.Singleton

interface TileIsValidAlgorithm{
    abstract fun canWalkOnTile(logicalCharacter: LogicalCharacter, tileLocation: TileLocation): Boolean
}

class AlwaysValid: TileIsValidAlgorithm{
    override fun canWalkOnTile(logicalCharacter: LogicalCharacter, tileLocation: TileLocation): Boolean {
        return true
    }
}

open class CanWalkOnTile(open val logicalTileTracker: LogicalTileTracker,
                    open val tacticalMapState: TacticalMapState): TileIsValidAlgorithm{
    override fun canWalkOnTile(logicalCharacter: LogicalCharacter, tileLocation: TileLocation): Boolean {
        val terrain = logicalTileTracker.getLogicalTileFromLocation(tileLocation)!!.terrainType

        return logicalCharacter.tacMapUnit.walkableTerrainTypes.contains(terrain) && isTileUnoccupied(tileLocation)
    }

    fun getCharacterAtLocation(tileLocation: TileLocation): LogicalCharacter? {
        return tacticalMapState.listOfCharacters.firstOrNull { it.tileLocation == tileLocation }
    }

    fun isTileUnoccupied(tileLocation: TileLocation): Boolean {
        return getCharacterAtLocation(tileLocation) == null
        && !isClosedDoor(tileLocation)
        && !logicalTileTracker.isWall(tileLocation)
    }

    private fun isClosedDoor(location:TileLocation): Boolean {
        if (!logicalTileTracker.isDoor(location)){
            return false
        }
        val doorEntity = logicalTileTracker.getEntitiesAtTile(location).filter{it is DoorEntity}.first() as DoorEntity
        return !doorEntity.isOpen
    }
}

public data class ActionResult(val logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                               val logicalCharacter: LogicalCharacter,
                               val butFirstMoveHere: TileLocation?,
                               val squaresTargetable: Collection<TileLocation>)

@Singleton
class TacticalMapAlgorithms @Inject constructor(override val logicalTileTracker: LogicalTileTracker,
                                                override val tacticalMapState: TacticalMapState)
    : CanWalkOnTile(logicalTileTracker, tacticalMapState) {

    val logicHooks: LogicHooks by lazy{
        GameModuleInjector.generateInstance(LogicHooks::class.java)
    }

    public fun getWhereCharacterCanMoveTo(character: LogicalCharacter): Collection<TileLocation> {
        val movesLeft = logicHooks.calculateAllowedUnitMovement(character)
        val tiles = getWalkableTileLocationsUpToNAway(movesLeft, character.tileLocation, character,
                CanWalkOnTile(logicalTileTracker, tacticalMapState))
        return tiles
    }

    fun getTileLocationsUpToNAway(n: Int, origin: TileLocation, character: LogicalCharacter): Collection<TileLocation>{
        return getWalkableTileLocationsUpToNAway(n, origin, character, AlwaysValid())
    }
    /**
     * e.g. , getting tiles up to 1 away will always return 4 tiles assuming all are passable.
     * Will not include the origin tile.
     */
    fun getWalkableTileLocationsUpToNAway(n: Int, origin: TileLocation, character: LogicalCharacter,
                                          tileIsValidAlgorithm: TileIsValidAlgorithm): Collection<TileLocation> {
        var iteration = n
        val tilesAlreadyProcessed = HashSet<TileLocation>()
        var nextSetOfTiles = HashSet<TileLocation>()
        var tilesToBeProcessed = hashSetOf(origin)
        while(tilesToBeProcessed.isNotEmpty() && iteration > 0){
            val nextTile = tilesToBeProcessed.first()
            tilesToBeProcessed.remove(nextTile)
            val neighbors = getPassableNeighbors(nextTile, character, tileIsValidAlgorithm)
            nextSetOfTiles.addAll(neighbors.filter{tilesAlreadyProcessed.doesNotContain(it)})
            tilesAlreadyProcessed.add(nextTile)
            if (tilesToBeProcessed.isEmpty()){
                tilesToBeProcessed.addAll(nextSetOfTiles)
                iteration--
            }
        }
        return tilesAlreadyProcessed
    }

    private fun getPassableNeighbors(nextTile: TileLocation, character: LogicalCharacter, tileIsValidAlgorithm: TileIsValidAlgorithm): Collection<TileLocation> {
       return logicalTileTracker.getNeighbors(nextTile).filter{isPassable(it, character, tileIsValidAlgorithm)}
    }

    private fun isPassable(loc: TileLocation, character: LogicalCharacter, tileIsValidAlgorithm: TileIsValidAlgorithm): Boolean {
        val tile = logicalTileTracker.tiles.firstOrNull{it.location == loc}
        if (tile == null){
            return false
        }
        return tileIsValidAlgorithm.canWalkOnTile(character, loc)
    }

    fun getCharactersWithinNumberOfTilesOfCharacter(range: Int, originCharacter: LogicalCharacter): Collection<LogicalCharacter> {
        val locationsWithinNTiles = getWalkableTileLocationsUpToNAway(range, originCharacter.tileLocation, originCharacter, AlwaysValid()).toMutableList()
        val characters = tacticalMapState.listOfCharacters.toMutableList()

        return characters.filter{it.tileLocation in locationsWithinNTiles}
    }
}
