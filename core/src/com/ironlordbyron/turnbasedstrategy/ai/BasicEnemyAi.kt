package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
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
                          val basicAiDecisions: BasicAiDecisions) : EnemyAi{

    override fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction> {
        if (thisCharacter.goal == null || thisCharacter.goal!!.shouldChangeGoal()){
            thisCharacter.goal = thisCharacter.tacMapUnit.metagoal.formulateNewGoal(thisCharacter)
        }
        val nextActions =  thisCharacter.goal!!.executeOnIntent(thisCharacter)
        thisCharacter.goal!!.formulateIntent(thisCharacter)
        return nextActions
    }

    private fun getNextAbilityUsage(thisCharacter: LogicalCharacter, locationAfterMove: TileLocation): AiPlannedAction.AbilityUsage? {
        var abilityUsage: AiPlannedAction.AbilityUsage? = null
        for (logicalAbilityAndEquipment in thisCharacter.abilities) {
            val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
            val targetableTilesFromThisSquare = ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter, logicalAbilityAndEquipment, locationAfterMove)
            if (!targetableTilesFromThisSquare.isEmpty()) {
                abilityUsage = AiPlannedAction.AbilityUsage(targetableTilesFromThisSquare.first(), logicalAbilityAndEquipment, thisCharacter)
                // Can make evaluation function later for telling which abilityEquipmentPair to use.
            }
        }
        if (abilityUsage != null){
            println("AbilityTargetingParameters AI will use: $abilityUsage")
        }
        return abilityUsage
    }

    // First priority: Can we hit an enemy with an abilityEquipmentPair from a reachable tile?  If so, DO IT.
    fun getNextMoveLocation(thisCharacter: LogicalCharacter) : TileLocation?{
        val reachableLocations  = mapAlgorithms.getWhereCharacterCanMoveTo(thisCharacter)
        // First: if we can target the enemy from a location we can reach?  GO THERE.
        for (reachableLocation in reachableLocations){
            for (logicalAbilityAndEquipment in thisCharacter.abilities){
                val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
                val targetableTilesFromThisSquare = ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter,logicalAbilityAndEquipment, reachableLocation)
                if (!targetableTilesFromThisSquare.isEmpty()){
                    return reachableLocation
                }
            }
        }

        val aiGridGraph = aiGridGraphFactory.createGridGraph(thisCharacter)

        val closestEnemy = aiGridGraph.acquireClosestEnemy(thisCharacter)
        if (closestEnemy == null){
            return null
        }

        // TODO; this doesn't exactly work how i want, but it's a low priority for fixing.
        val targetEndTile = aiGridGraph.findClosestWalkableTileTo(closestEnemy.tileLocation, thisCharacter)
        if (targetEndTile == null){
            println("WARNING: Could not acquire target tile for ai.")
            return null
        }

        val pathToEnemy = aiGridGraph.acquireBestPathTo(thisCharacter, targetEndTile,
                allowEndingOnLastTile = true)

        if (pathToEnemy == null){
            println("BasicEnemyAi:Could not find path from ${thisCharacter.tileLocation} to ${closestEnemy.tileLocation} for ${thisCharacter.tacMapUnit.templateName}")
            return null
        }
        if (pathToEnemy.isEmpty()){
            return null
        }

        val moverate = thisCharacter.tacMapUnit.movesPerTurn
        if (pathToEnemy.size > moverate - 2){
            return pathToEnemy.toList()[moverate - 2]
        }

        return pathToEnemy.last()
        // TODO: Ensure tile isn't occupied by enemy (if it is, stop one short)
    }

}