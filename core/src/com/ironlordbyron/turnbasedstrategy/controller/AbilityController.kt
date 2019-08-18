package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbilityController @Inject constructor(val tacticalMapState: TacticalMapState,
                                            val mapHighlighter: MapHighlighter,
                                            val mapAlgorithms: TacticalMapAlgorithms) {

    /**
     * Represents a person clicking on an abilityEquipmentPair usage button (which then requires additional parameters.)
     */
    fun signalIntentToActOnAbility(characterUsingAbility: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment){
        val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
        val tilesInRange = ability.getValidAbilityTargetSquares(characterUsingAbility, logicalAbilityAndEquipment, characterUsingAbility.tileLocation)
        mapHighlighter.killHighlights()
        mapHighlighter.highlightTiles(tilesInRange, HighlightType.RED_TILE, tag = "attack")
    }

    fun isSquareInRangeForAbility(characterUsingAbility: LogicalCharacter,
                                  logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                                  targetCharacter: LogicalCharacter?,
                                  location: TileLocation) : Boolean{
        val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
        val isValid = ability.isValidTarget(location, targetCharacter, characterUsingAbility, logicalAbilityAndEquipment)
        return isValid
    }


    fun canUseAbilityOnSquare(characterUsingAbility: LogicalCharacter,
                              logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                              targetCharacter: LogicalCharacter?,
                              location: TileLocation) : Boolean{
        val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
        val isValid = ability.getSquaresThatCanActuallyBeTargetedByAbility(characterUsingAbility, sourceSquare = characterUsingAbility.tileLocation,
                logicalAbilityAndEquipment = logicalAbilityAndEquipment)
                .contains(location)
        return isValid
    }
    fun useAbility(characterUsingAbility: LogicalCharacter,
                   logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                   targetCharacter: LogicalCharacter?,
                   location: TileLocation){
        val ability = logicalAbilityAndEquipment.ability.abilityTargetingParameters
        ability.activateAbility(location, targetCharacter, characterUsingAbility, logicalAbilityAndEquipment)
    }
}
