package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.HighlightType
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbilityController @Inject constructor(val tacticalMapState: TacticalMapState,
                                            val mapHighlighter: MapHighlighter,
                                            val mapAlgorithms: TacticalMapAlgorithms) {

    /**
     * Represents a person clicking on an ability usage button (which then requires additional parameters.)
     */
    fun SignalIntentToActOnAbility(characterUsingAbility: LogicalCharacter, logicalAbility: LogicalAbility){
        if(logicalAbility.abilityClass == AbilityClass.TARGETED_ABILITY){
            val tilesInRange = mapAlgorithms.getTilesInRangeOfAbility(characterUsingAbility, logicalAbility)
            mapHighlighter.killHighlights()
            mapHighlighter.highlightTiles(tilesInRange, HighlightType.RED_TILE)
        }
    }
}
