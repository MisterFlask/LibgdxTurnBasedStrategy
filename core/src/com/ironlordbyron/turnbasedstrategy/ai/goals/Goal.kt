package com.ironlordbyron.turnbasedstrategy.ai.goals

import com.ironlordbyron.turnbasedstrategy.ai.AiPlannedAction
import com.ironlordbyron.turnbasedstrategy.ai.Intent
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter

interface Goal{
    fun formulateIntent(thisCharacter: LogicalCharacter) : Intent

    fun executeOnIntent(thisCharacter: LogicalCharacter) : List<AiPlannedAction>

    fun shouldChangeGoal() : Boolean {
        return false
    }
}