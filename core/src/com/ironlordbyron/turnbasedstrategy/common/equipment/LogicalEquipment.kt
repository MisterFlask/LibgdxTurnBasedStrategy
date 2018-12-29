package com.ironlordbyron.turnbasedstrategy.common.equipment

import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility

public data class LogicalEquipment(val name: String,
                                   val equipmentClass: EquipmentClass,
                                   val abilityEnabled: Collection<LogicalAbility> = listOf())