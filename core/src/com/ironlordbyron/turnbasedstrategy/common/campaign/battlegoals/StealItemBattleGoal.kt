package com.ironlordbyron.turnbasedstrategy.common.campaign.battlegoals

import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.common.wrappers.BattleGoal
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject

public class StealItemBattleGoal(val item: LogicalEquipment,
                                 override val name: String,
                                 override val description: String) : BattleGoal {
    val tacMapState by LazyInject(TacticalMapState::class.java)
    override fun isGoalMet(): Boolean {
        if (tacMapState.evacuatedUnits.any{it.pickedUpItems.any{pickup -> pickup.uuid == item.uuid}}){
            return true
        }else{
            return false
        }
    }
}

public class StealItemBattleGoalGenerator()