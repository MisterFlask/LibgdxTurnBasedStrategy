package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.randomElement
import javax.inject.Inject

class ImmobileEnemyAi @Inject constructor() : EnemyAi{
    override fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction> {
        val targetableSquare = getTargetableSquare(thisCharacter)
        if (targetableSquare == null){
            return listOf()
        }
        return listOf(AiPlannedAction.AbilityUsage(targetableSquare, thisCharacter.abilities.first(), thisCharacter))
    }

    // it just grabs the first abilityEquipmentPair available.  FOR NOW
    private fun getTargetableSquare(thisCharacter: LogicalCharacter): TileLocation? {
        if (thisCharacter.abilities.isEmpty()){
            return null
        }
        val ability = thisCharacter.abilities.randomElement()
        return ability.ability.abilityTargetingParameters.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter,
                ability, thisCharacter.tileLocation).firstOrNull()
    }
}