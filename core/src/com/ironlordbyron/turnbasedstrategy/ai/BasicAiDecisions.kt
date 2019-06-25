package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.randomElement
import com.ironlordbyron.turnbasedstrategy.tileentity.CityTileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.toCharacter
import javax.inject.Inject


val MIN_ATTACK_DISTANCE = 10
public class BasicAiDecisions @Inject constructor (val mapAlgorithms: TacticalMapAlgorithms,
                                                   val aiGridGraphFactory: AiGridGraphFactory,
                                                   val tacticalMapState: TacticalMapState,
                                                   val pathfinderFactory: PathfinderFactory,
                                                   val logicalTileTracker: LogicalTileTracker){


    public fun formulateIntent(thisCharacter: LogicalCharacter) : Intent{
        if (thisCharacter.tacMapUnit.enemyAiType == EnemyAiType.BASIC){
            val shouldFindEnemyToAttack = shouldAttackClosestEnemy(thisCharacter)
            if (shouldFindEnemyToAttack) {
                return getAttackIntentForThisTurn(thisCharacter)
            } else{
                return Intent.Move()
            }

        }
        return Intent.Other()
    }

    private fun shouldAttackClosestEnemy(thisCharacter: LogicalCharacter): Boolean {
        val closestEnemy = tacticalMapState.closestPlayerControlledCharacterTo(thisCharacter)
        val shouldFindEnemyToAttack = closestEnemy != null && closestEnemy.tileLocation.distanceTo(thisCharacter.tileLocation) < MIN_ATTACK_DISTANCE
        return shouldFindEnemyToAttack
    }

    public fun beelineTowardNearestCity(thisCharacter: LogicalCharacter) : List<AiPlannedAction>{
        val closestCity = getClosestMatchingEntity(thisCharacter){
            it is CityTileEntity
        }
        if (closestCity == null){
            return listOf()
        }
        val aiGridGraph = aiGridGraphFactory.createGridGraph(thisCharacter)
        val pathToCity = aiGridGraph.acquireBestPathTo(thisCharacter, closestCity.tileLocation, allowEndingOnLastTile = true)
        if (pathToCity == null){
            throw Exception("No path to closest city: $closestCity")
        }
        return listOf(AiPlannedAction.MoveToTile(getFurthestAllowedSpotOnPath(thisCharacter, pathToCity)))
    }

    public fun getClosestMatchingEntity(thisCharacter: LogicalCharacter, predicate: (TileEntity) -> Boolean): TileEntity? {
        val entities = logicalTileTracker.tileEntities
                .filter{predicate.invoke(it)}
        val closest = entities.minBy{it.tileLocation.distanceTo(thisCharacter.tileLocation)}
        return closest
    }

    public fun isIntentStillPossible(thisCharacter: LogicalCharacter) : Boolean{
        val intent = thisCharacter.intent
        when(intent){
            is Intent.Attack -> {
                return canTargetCharacterWithAbility(
                        thisCharacter,
                        intent.logicalCharacterUuid.toCharacter(),
                        intent.intentType)
            }
            else -> {
                val abilitiesByIntent = thisCharacter.abilitiesForIntent(thisCharacter.intent.intentType)
                for (ability in abilitiesByIntent){
                    if (ability.ability.customAbilityAi == null){
                        throw IllegalStateException("Could not find custom AI for non-attack ability: ${ability.ability.name}")
                    }
                }
                val canUseAtLeastOneAbility = abilitiesByIntent.any{it.ability.customAbilityAi!!.canUseAbility(thisCharacter)}
                return canUseAtLeastOneAbility
            }
        }
    }


    public fun executeOnIntent(thisCharacter: LogicalCharacter): List<AiPlannedAction>{
        if (thisCharacter.intent is Intent.None || thisCharacter.intent is Intent.Move){
            thisCharacter.intent = formulateIntent(thisCharacter)
        }
        when(thisCharacter.intent.intentType){
            IntentType.ATTACK -> {
                Logging.DebugCombatLogic("Character ${thisCharacter.tacMapUnit.templateName} is attempting attack")
                val attackIntent = thisCharacter.intent as Intent.Attack
                if (canTargetCharacterWithAbility(thisCharacter, tacticalMapState.getCharacterFromId(attackIntent.logicalCharacterUuid), thisCharacter.intent.intentType)){
                    val plannedActions = getNecessaryMoveForTargetingCharacterWithAbility(thisCharacter, tacticalMapState.getCharacterFromId(attackIntent.logicalCharacterUuid), thisCharacter.intent.intentType)!!
                    return plannedActions
                }
                thisCharacter.intent = formulateIntent(thisCharacter)
                return executeOnIntent(thisCharacter)
            }
            IntentType.MOVE -> {
                if (!shouldAttackClosestEnemy(thisCharacter)){
                    return beelineTowardNearestCity(thisCharacter)
                }
                Logging.DebugCombatLogic("Character ${thisCharacter.tacMapUnit.templateName} is attempting movement")

                val bestPathToClosestPlayerUnit = pathfindToClosestPlayerUnit(thisCharacter)
                if (bestPathToClosestPlayerUnit == null){
                    println("Could not find best path to tile ")
                    return listOf()
                }
                if (bestPathToClosestPlayerUnit.size == 0){
                    return listOf()
                }
                val nextMove = AiPlannedAction.MoveToTile(bestPathToClosestPlayerUnit.last().location)
                return listOf(nextMove)
            }
            IntentType.NONE, IntentType.DEFEND, IntentType.OTHER  -> {
                println("Not supported: ${thisCharacter.intent.intentType}" )
                return listOf()
            }
        }
    }

    public fun pathfindToClosestPlayerUnit(thisCharacter: LogicalCharacter) : Collection<PathfindingTileLocation>?{
        val pathfinder = pathfinderFactory.createGridGraph(thisCharacter)
        val playerUnits = tacticalMapState.listOfPlayerCharacters
        var currentBestPath: Collection<PathfindingTileLocation>? = null
        for (targetUnit in playerUnits){
            val path = pathfinder.acquireBestPathTo(thisCharacter, targetUnit.tileLocation, false)
            if (path == null){
                continue
            }
            if (currentBestPath == null){
                currentBestPath = path
            }
            if (currentBestPath.size > path.size){
                currentBestPath = path
            }
        }
        return currentBestPath
    }

    public fun canTargetCharacterWithAbility(thisCharacter: LogicalCharacter, targetCharacter: LogicalCharacter, intent: IntentType): Boolean {
        return getNecessaryMoveForTargetingCharacterWithAbility(thisCharacter, targetCharacter, intent) != null
    }

    public fun getNecessaryMoveForTargetingCharacterWithAbility(thisCharacter: LogicalCharacter,
                                                                 targetCharacter: LogicalCharacter,
                                                                 intent: IntentType) : List<AiPlannedAction>?{
        for (abilityAndEquipment in thisCharacter.abilitiesForIntent(intent)){
            for (tile in mapAlgorithms.getWhereCharacterCanMoveTo(thisCharacter)){
                if (abilityAndEquipment.getSquaresInRangeOfAbility(tile, thisCharacter)
                                .contains(targetCharacter.tileLocation)){
                    return listOf(
                            AiPlannedAction.MoveToTile(tile),
                            AiPlannedAction.AbilityUsage(
                            targetCharacter.tileLocation,
                            abilityAndEquipment,
                            thisCharacter))
                }
            }
        }
        return null
    }

    // Figures out who to attack.
    public fun getAttackIntentForThisTurn(thisCharacter: LogicalCharacter): Intent {
        for (abilityAndEquipment in thisCharacter.abilitiesForIntent(IntentType.ATTACK)){
            if (!canPerform(abilityAndEquipment, thisCharacter)){
                Logging.DebugCombatLogic("Enemy ${thisCharacter.tacMapUnit.templateName} cannot perform ${abilityAndEquipment.ability.name}; continuing")
                continue
            }
            val plannedAction = getAbilityUsagePairedWithRequiredMove(thisCharacter, abilityAndEquipment)
                    .first{it is AiPlannedAction.AbilityUsage} as AiPlannedAction.AbilityUsage
            val targetedCharacter = tacticalMapState.characterAt(plannedAction.squareToTarget)
            if (targetedCharacter == null){
                throw Exception("Attempted to perform $plannedAction but could not find character at ${plannedAction.squareToTarget}")
            }
            return Intent.Attack(targetedCharacter!!.id)
        }
        val first = thisCharacter.abilitiesForIntent(IntentType.ATTACK).firstOrNull()
        if (first == null){
            throw IllegalStateException("Could not find ability for intent ${thisCharacter.intent}")
        }
        val nextLocation = getNextMoveLocationForAbility(thisCharacter, first)
        if (nextLocation == null){
            Logging.DebugCombatLogic("Enemy ${thisCharacter.tacMapUnit.templateName} cannot find ability t operform or location to go to")
            return Intent.None()
        }

        Logging.DebugCombatLogic("Enemy ${thisCharacter.tacMapUnit.templateName} cannot find ability it can perform; moving to closest enemy unit")
        return Intent.Move()
    }

    private fun canPerform(abilityAndEquipment: LogicalAbilityAndEquipment, thisCharacter: LogicalCharacter): Boolean {
        val nextMove = getAbilityUsagePairedWithRequiredMove(thisCharacter, abilityAndEquipment)
        return nextMove.any{it is AiPlannedAction.AbilityUsage}
    }

    public fun getAbilityUsageFromLocation(thisCharacter: LogicalCharacter, fromLocation: TileLocation, logicalAbilityAndEquipment: LogicalAbilityAndEquipment): AiPlannedAction.AbilityUsage? {
        var abilityUsage: AiPlannedAction.AbilityUsage? = null
        val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
        val targetableTilesFromThisSquare = ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter, logicalAbilityAndEquipment, fromLocation)
        if (!targetableTilesFromThisSquare.isEmpty()) {
            abilityUsage = AiPlannedAction.AbilityUsage(targetableTilesFromThisSquare.first(), logicalAbilityAndEquipment, thisCharacter)
            // Can make evaluation function later for telling which abilityEquipmentPair to use.
        }
        if (abilityUsage != null){
            println("AbilityTargetingParameters AI will use: $abilityUsage")
        }
        return abilityUsage
    }

    /**
     * This returns a list of things for the AI to do.  The priority is to attempt to use the provided ability; if it can't, returns
     * a singleton list of the moves required to use that ability.
     */
    fun getAbilityUsagePairedWithRequiredMove(thisCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment): List<AiPlannedAction> {
        val nextMove = getNextMoveLocationForAbility(thisCharacter, logicalAbilityAndEquipment)
        if (nextMove != null){
            Logging.DebugPathfinding("AI's attempted move location: $nextMove")
        }
        val locationAfterMove = nextMove?:thisCharacter.tileLocation
        var abilityUsage: AiPlannedAction.AbilityUsage? = getAbilityUsageFromLocation(thisCharacter, locationAfterMove, logicalAbilityAndEquipment)
        val listOfActions = ArrayList<AiPlannedAction>()
        if (nextMove != null){
            listOfActions.add(AiPlannedAction.MoveToTile(nextMove))
        }
        if (abilityUsage != null){
            listOfActions.add(abilityUsage)
        }
        if (listOfActions.size == 2){
            Logging.DebugCombatLogic("Planned actions: $listOfActions, current location = ${thisCharacter.tileLocation}")
        }
        return listOfActions
    }

    // First priority: Can we hit an enemy with an abilityEquipmentPair from a reachable tile?  If so, DO IT.
    // Otherwise just get as close as possible to the enemy.
    fun getNextMoveLocationForAbility(thisCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment) : TileLocation?{
        val reachableLocations  = mapAlgorithms.getWhereCharacterCanMoveTo(thisCharacter)
        // First: if we can target the enemy from a location we can reach?  GO THERE.
        for (reachableLocation in reachableLocations){
            val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
            val targetableTilesFromThisSquare = ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter,logicalAbilityAndEquipment, reachableLocation)
            if (!targetableTilesFromThisSquare.isEmpty()){
                return reachableLocation
            }
        }

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
            println("Could not find path from ${thisCharacter.tileLocation} to ${closestEnemy.tileLocation} for ${thisCharacter.tacMapUnit.templateName}")
            return null
        }
        if (pathToEnemy.isEmpty()){
            return null
        }

        return getFurthestAllowedSpotOnPath(thisCharacter, pathToEnemy)
        // TODO: Ensure tile isn't occupied by enemy (if it is, stop one short)
    }

    private fun getFurthestAllowedSpotOnPath(thisCharacter: LogicalCharacter, pathToEnemy: Collection<PathfindingTileLocation>): TileLocation {
        val moverate = thisCharacter.tacMapUnit.movesPerTurn
        if (pathToEnemy.size > moverate - 2) {
            return pathToEnemy.toList()[moverate - 2].location
        }

        return pathToEnemy.last().location
    }

}