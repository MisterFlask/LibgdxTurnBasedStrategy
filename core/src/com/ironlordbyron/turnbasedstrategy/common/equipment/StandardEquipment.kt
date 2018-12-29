package com.ironlordbyron.turnbasedstrategy.common.equipment

import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities

object StandardEquipment{
    val sword = Equipment("Sword", EquipmentClass.MELEE_WEAPON_LARGE, abilityEnabled = listOf(StandardAbilities.MeleeAttack))
}