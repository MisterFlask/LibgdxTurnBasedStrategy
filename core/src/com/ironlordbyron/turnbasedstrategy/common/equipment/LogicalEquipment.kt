package com.ironlordbyron.turnbasedstrategy.common.equipment

import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbilityEffect

public data class LogicalEquipment(val name: String,
                                   val equipmentClass: EquipmentClass,
                                   val abilityEnabled: Collection<LogicalAbility> = listOf(),
                                   val effectToApply: LogicalAbilityEffect? = null)