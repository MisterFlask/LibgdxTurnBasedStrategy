package com.ironlordbyron.turnbasedstrategy.missiongen

import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import javax.inject.Singleton

@Singleton
class VictoryChecker{
    val tacMapState by LazyInject(TacticalMapState::class.java)
    fun isBattleOver() : Boolean{
        return tacMapState.listOfPlayerCharacters.isEmpty()
    }
}


val victoryChecker by LazyInject(VictoryChecker::class.java)

annotation class RunOnEveryVisibleAction