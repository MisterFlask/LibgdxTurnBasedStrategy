package com.ironlordbyron.turnbasedstrategy.missiongen

import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import javax.inject.Singleton

@Singleton
class VictoryChecker{
    val globalTacMapState by LazyInject(GlobalTacMapState::class.java)
    val tacMapState by LazyInject(TacticalMapState::class.java)
    fun isBattleOver() : Boolean{
        return tacMapState.listOfPlayerCharacters.isEmpty() && globalTacMapState.isMissionStarted
    }
}
