package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider

/**
 * Attempts to move to closest enemy.
 * Will find the best path to that enemy.
 * Will move to closest usable tile.
 */
public class BasicEnemyAi(val tiledMapOperationsHandler: TiledMapOperationsHandler,
                          val tacticalMapState: TacticalMapState,
                          val tileMapProvider: TileMapProvider,
                          val aiGridGraphFactory: AiGridGraphFactory,
                          val mapAlgorithms: TacticalMapAlgorithms,
                          val abilityFactory: AbilityFactory) : EnemyAi{

    override fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction> {
        val nextMove = getNextMoveLocation(thisCharacter)
        val locationAfterMove = nextMove?:thisCharacter.tileLocation
        var abilityUsage : AiPlannedAction.AbilityUsage? = null
        for (logicalAbility in thisCharacter.abilities){
            val ability = abilityFactory.acquireAbility(logicalAbility)
            val targetableTilesFromThisSquare = ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter, locationAfterMove)
            if (!targetableTilesFromThisSquare.isEmpty()){
                abilityUsage = AiPlannedAction.AbilityUsage(targetableTilesFromThisSquare.first(), logicalAbility)
                // Can make evaluation function later for telling which ability to use.
            }
        }
        val listOfActions = ArrayList<AiPlannedAction>()
        if (nextMove != null){
            listOfActions.add(AiPlannedAction.MoveToTile(nextMove))
        }
        if (abilityUsage != null){
            listOfActions.add(abilityUsage)
        }
        return listOfActions
    }

    // First priority: Can we hit an enemy with an ability from a reachable tile?  If so, DO IT.
    fun getNextMoveLocation(thisCharacter: LogicalCharacter) : TileLocation?{
        val reachableLocations  = mapAlgorithms.getWhereCharacterCanMoveTo(thisCharacter)
        // First: if we can target the enemy from a location we can reach?  GO THERE.
        for (reachableLocation in reachableLocations){
            for (logicalAbility in thisCharacter.abilities){
                val ability = abilityFactory.acquireAbility(logicalAbility)
                val targetableTilesFromThisSquare = ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter, reachableLocation)
                if (!targetableTilesFromThisSquare.isEmpty()){
                    return reachableLocation
                }
            }
        }

        val map = tileMapProvider.tiledMap
        val aiGridGraph = aiGridGraphFactory.createGridGraph(thisCharacter)

        val closestEnemy = aiGridGraph.acquireClosestEnemy(thisCharacter)
        if (closestEnemy == null){
            return null
        }

        // TODO; this doesn't exactly work how i want, but it's a low priority for fixing.
        val targetEndTile = aiGridGraph.findClosestUnoccupiedTileTo(closestEnemy.tileLocation, allowEndingOnLastTile = false)
        if (targetEndTile == null){
            println("WARNING: Could not acquire target tile for ai.")
            return null
        }

        val pathToEnemy = aiGridGraph.acquireBestPathTo(thisCharacter, targetEndTile,
                allowEndingOnLastTile = true)

        if (pathToEnemy == null){
            return null
        }
        if (pathToEnemy.isEmpty()){
            return null
        }

        val moverate = thisCharacter.tacMapUnit.movesPerTurn
        if (pathToEnemy.size > moverate - 2){
            return pathToEnemy.toList()[moverate - 2].location
        }

        return pathToEnemy.last().location
        // TODO: Ensure tile isn't occupied by enemy (if it is, stop one short)
    }

}