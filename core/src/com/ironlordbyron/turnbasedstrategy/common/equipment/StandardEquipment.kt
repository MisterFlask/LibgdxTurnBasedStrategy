package com.ironlordbyron.turnbasedstrategy.common.equipment

import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities

object StandardEquipment{
    val sword = LogicalEquipment("Sword", EquipmentClass.MELEE_WEAPON_LARGE, abilityEnabled = listOf(StandardAbilities.SwordSlashAttack))
    val flamethrower = LogicalEquipment("Flamethrower", EquipmentClass.MELEE_WEAPON_LARGE, abilityEnabled = listOf(StandardAbilities.RangedAttack))
}