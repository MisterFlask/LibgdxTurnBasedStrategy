package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation

public enum class EnemyAiType{
    BASIC
}

interface EnemyAi{
    fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction>
}



sealed class AiPlannedAction{
    data class MoveToTile(val to : TileLocation) : AiPlannedAction()
    data class AttackCharacter(val character: LogicalCharacter)
}