package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbilityController @Inject constructor(val tacticalMapState: TacticalMapState,
                                            val mapHighlighter: MapHighlighter,
                                            val mapAlgorithms: TacticalMapAlgorithms,
                                            val abilityFactory: AbilityFactory) {

    /**
     * Represents a person clicking on an ability usage button (which then requires additional parameters.)
     */
    fun signalIntentToActOnAbility(characterUsingAbility: LogicalCharacter, logicalAbility: LogicalAbility){
        val ability = abilityFactory.acquireAbility(logicalAbility)
        val tilesInRange = ability.getValidAbilityTargetSquares(characterUsingAbility)
        mapHighlighter.killHighlights()
        mapHighlighter.highlightTiles(tilesInRange, HighlightType.RED_TILE)
    }

    fun canUseAbilityOnSquare(characterUsingAbility: LogicalCharacter,
                              logicalAbility: LogicalAbility,
                              targetCharacter: LogicalCharacter?,
                              location: TileLocation) : Boolean{
        val ability = abilityFactory.acquireAbility(logicalAbility)
        val isValid = ability.isValidTarget(location, targetCharacter, characterUsingAbility)
        return isValid
    }

    fun useAbility(characterUsingAbility: LogicalCharacter,
                   logicalAbility: LogicalAbility,
                   targetCharacter: LogicalCharacter?,
                   location: TileLocation){
        val ability = abilityFactory.acquireAbility(logicalAbility)
        ability.activateAbility(location, targetCharacter, characterUsingAbility)
    }
}
