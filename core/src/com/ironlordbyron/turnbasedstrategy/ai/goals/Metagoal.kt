package com.ironlordbyron.turnbasedstrategy.ai.goals

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter

public interface Metagoal{
    fun formulateNewGoal(logicalCharacter: LogicalCharacter) : Goal
}
public class ConquerCityMetagoal : Metagoal{
    override fun formulateNewGoal(logicalCharacter: LogicalCharacter): Goal {
        return ConquerCityGoal()
    }
}

// TODO: Attack meta goal doesn't function?
public class AttackMetaGoal: Metagoal{
    override fun formulateNewGoal(logicalCharacter: LogicalCharacter): Goal {
        return AttackGoal()
    }
}