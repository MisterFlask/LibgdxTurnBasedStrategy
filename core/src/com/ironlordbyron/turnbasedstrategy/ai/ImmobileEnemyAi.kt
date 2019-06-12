package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import javax.inject.Inject

class ImmobileEnemyAi @Inject constructor(val abilityFactory: AbilityFactory) : EnemyAi{
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
        val ability = abilityFactory.acquireAbility(thisCharacter.abilities.first())
        return ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter, thisCharacter.abilities.first().equipment, thisCharacter.tileLocation).firstOrNull()
    }
}