package com.ironlordbyron.turnbasedstrategy.missiongen

import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject

class VictoryChecker{
    val tacMapState by LazyInject(TacticalMapState::class.java)
    fun isBattleOver() : Boolean{
        return tacMapState.listOfPlayerCharacters.isEmpty()
    }



}