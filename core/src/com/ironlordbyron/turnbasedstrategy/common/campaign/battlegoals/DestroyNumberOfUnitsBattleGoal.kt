package com.ironlordbyron.turnbasedstrategy.common.campaign.battlegoals

import com.ironlordbyron.turnbasedstrategy.common.campaign.BattleGoalGenerator
import com.ironlordbyron.turnbasedstrategy.common.campaign.RegisteredBattleGoal
import com.ironlordbyron.turnbasedstrategy.common.wrappers.BattleGoal
import com.ironlordbyron.turnbasedstrategy.controller.tacMapState


@RegisteredBattleGoal
public fun destroyNumberOfUnitsBattleGoalGen(): BattleGoalGenerator {
    return DestroyNumberOfUnitsBattleGoalGenerator()
}

class DestroyNumberOfUnitsBattleGoalGenerator() : BattleGoalGenerator {
    override fun generateBattleGoal(): BattleGoal {
        return DestroyNumberOfUnitsBattleGoal(5)
    }

}


public class DestroyNumberOfUnitsBattleGoal(val numToDestroy: Int,
                                            override val name: String = "Kill Demons",
                                            override val description: String = "before the end of combat, kill $numToDestroy non-minion enemies.") : BattleGoal {
    override fun isGoalMet(): Boolean {
        return nonMinionsDead() >= numToDestroy
    }

    fun nonMinionsDead() : Int {
        return tacMapState.deadCharacters.filter{it.nonMinionEnemy}.count()
    }

    override fun getGoalProgressString(): String {
        if (!isGoalMet()){
            return "${nonMinionsDead()} banished so far"
        }else{
            return super.getGoalProgressString()
        }

    }

}