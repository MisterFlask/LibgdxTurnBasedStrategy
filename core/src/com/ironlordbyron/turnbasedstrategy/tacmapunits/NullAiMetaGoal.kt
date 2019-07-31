package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.AiPlannedAction
import com.ironlordbyron.turnbasedstrategy.ai.Intent
import com.ironlordbyron.turnbasedstrategy.ai.goals.Goal
import com.ironlordbyron.turnbasedstrategy.ai.goals.Metagoal
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter

class NullAiMetaGoal : Metagoal {
    override fun formulateNewGoal(logicalCharacter: LogicalCharacter): Goal {
        return NullGoal()
    }

}

class NullGoal : Goal {
    override fun describe(): String {
       return "Goal:Null"
    }
    override fun formulateIntent(thisCharacter: LogicalCharacter): Intent {
        return Intent.None()
    }

    override fun executeOnIntent(thisCharacter: LogicalCharacter): List<AiPlannedAction> {
        return listOf()
    }

}
