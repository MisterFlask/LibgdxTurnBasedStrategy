package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityFactory
import javax.inject.Inject

class ImmobileEnemyAi @Inject constructor(
                                          val abilityFactory: AbilityFactory) : EnemyAi{
    override fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction> {
        return listOf(AiPlannedAction.AbilityUsage(getTargetableSquare(thisCharacter), thisCharacter.abilities.first()))
    }

    private fun getTargetableSquare(thisCharacter: LogicalCharacter): TileLocation {
        val ability = abilityFactory.acquireAbility(thisCharacter.abilities.first())
        return ability.getSquaresThatCanActuallyBeTargetedByAbility(thisCharacter).first()
    }
}