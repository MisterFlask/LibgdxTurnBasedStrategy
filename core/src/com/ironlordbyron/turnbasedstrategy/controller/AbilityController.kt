package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbilityController @Inject constructor(val tacticalMapState: TacticalMapState,
                                            val mapHighlighter: MapHighlighter,
                                            val mapAlgorithms: TacticalMapAlgorithms,
                                            val abilityFactory: AbilityFactory) {

    /**
     * Represents a person clicking on an abilityEquipmentPair usage button (which then requires additional parameters.)
     */
    fun signalIntentToActOnAbility(characterUsingAbility: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment){
        val ability = abilityFactory.acquireAbility(logicalAbilityAndEquipment)
        val tilesInRange = ability.getValidAbilityTargetSquares(characterUsingAbility, logicalAbilityAndEquipment.equipment)
        mapHighlighter.killHighlights()
        mapHighlighter.highlightTiles(tilesInRange, HighlightType.RED_TILE)
    }

    fun isSquareInRangeForAbility(characterUsingAbility: LogicalCharacter,
                                  logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                                  targetCharacter: LogicalCharacter?,
                                  location: TileLocation) : Boolean{
        val ability = abilityFactory.acquireAbility(logicalAbilityAndEquipment)
        val isValid = ability.isValidTarget(location, targetCharacter, characterUsingAbility, logicalAbilityAndEquipment.equipment)
        return isValid
    }


    fun canUseAbilityOnSquare(characterUsingAbility: LogicalCharacter,
                              logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                              targetCharacter: LogicalCharacter?,
                              location: TileLocation) : Boolean{
        val ability = abilityFactory.acquireAbility(logicalAbilityAndEquipment)
        val isValid = ability.getSquaresThatCanActuallyBeTargetedByAbility(characterUsingAbility, sourceSquare = characterUsingAbility.tileLocation,
                equipment = logicalAbilityAndEquipment.equipment)
                .contains(location)
        return isValid
    }
    fun useAbility(characterUsingAbility: LogicalCharacter,
                   logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                   targetCharacter: LogicalCharacter?,
                   location: TileLocation){
        val ability = abilityFactory.acquireAbility(logicalAbilityAndEquipment)
        ability.activateAbility(location, targetCharacter, characterUsingAbility, logicalAbilityAndEquipment.equipment)
    }
}
