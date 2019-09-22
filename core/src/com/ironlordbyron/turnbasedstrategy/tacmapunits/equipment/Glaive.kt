package com.ironlordbyron.turnbasedstrategy.tacmapunits.equipment

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment

public fun glaive() : LogicalEquipment{
    return LogicalEquipment("Glaive", EquipmentClass.GLAIVE, listOf(sweepAbility()))
}

fun sweepAbility(): LogicalAbility {
    return LogicalAbility("Sweep",
            AbilitySpeed.ENDS_TURN,
            2, null, null, 1, "Capable of a sweeping AoE attack.",
            AbilityClass.TARGETED_ATTACK_ABILITY, false, RequiredTargetType.ENEMY_ONLY, listOf(),
            null, null, areaOfEffect = AreaOfEffect.SweepAoeStyle(2))
}




