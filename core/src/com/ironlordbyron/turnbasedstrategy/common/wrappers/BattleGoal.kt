package com.ironlordbyron.turnbasedstrategy.common.wrappers

import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.controller.tacMapState
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject


public interface BattleGoal{
    val name: String
    val description: String

    fun isGoalMet(): Boolean
    fun getGoalProgressString() : String {
        return if (isGoalMet()) "COMPLETE" else "INCOMPLETE"
    }
}
