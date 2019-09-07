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

// todo: gussy this up
public class DestroyOrganBattleGoal(val unitTemplateIdToDestroy: String,
                                    override val name: String = "Destroy $unitTemplateIdToDestroy",
                                    override val description: String = "Before the end of combat, destroy $unitTemplateIdToDestroy") : BattleGoal{
    val globalTacMapState by LazyInject(GlobalTacMapState::class.java)
    val tacMapState by LazyInject(TacticalMapState::class.java)
    override fun isGoalMet(): Boolean {
        return tacMapState.deadCharacters.firstOrNull{it.templateId == unitTemplateIdToDestroy} != null
    }

}

public class DestroyNumberOfUnitsBattleGoal(val numToDestroy: Int,
                                            override val name: String = "Kill Demons",
                                            override val description: String = "before the end of combat, kill $numToDestroy non-minion enemies.") : BattleGoal{
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