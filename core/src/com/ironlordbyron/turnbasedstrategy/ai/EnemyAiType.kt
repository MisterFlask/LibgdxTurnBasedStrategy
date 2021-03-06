package com.ironlordbyron.turnbasedstrategy.ai

import com.ironlordbyron.turnbasedstrategy.common.LogicalAbilityAndEquipment
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import java.util.*

public enum class EnemyAiType{
    BASIC,

    IMMOBILE_UNIT
}

interface EnemyAi{
    fun getNextActions(thisCharacter: LogicalCharacter): List<AiPlannedAction>
}


enum class IntentType{
    ATTACK,
    DEFEND,
    MOVE,
    NONE,
    OTHER
}
sealed class Intent(val intentType: IntentType){
    data class Attack(val logicalCharacterUuid: UUID): Intent(IntentType.ATTACK)
    class Defend(): Intent(IntentType.DEFEND)
    class Move: Intent(IntentType.MOVE)
    class None: Intent(IntentType.NONE)
    class Other: Intent(IntentType.OTHER)
}



interface AiPlannedAction{
    data class MoveToTile(val to : TileLocation) : AiPlannedAction
    data class AbilityUsage(val squareToTarget: TileLocation,
                            val ability: LogicalAbilityAndEquipment,
                            val sourceCharacter: LogicalCharacter): AiPlannedAction{

        public override fun toString(): String{
            return "AbilityTargetingParameters: $squareToTarget, ${ability.ability.name}"
        }
    }
}