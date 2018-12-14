package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility

public enum class EnemyAiType{
    BASIC
}

interface EnemyAi{
    fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction>
}



interface AiPlannedAction{
    data class MoveToTile(val to : TileLocation) : AiPlannedAction
    data class AbilityUsage(val squareToTarget: TileLocation, val ability: LogicalAbility): AiPlannedAction
}