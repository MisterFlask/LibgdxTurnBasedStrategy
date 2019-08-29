package com.ironlordbyron.turnbasedstrategy.common.equipment

import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbilityEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import java.util.*

public data class LogicalEquipment(val name: String,
                                   val equipmentClass: EquipmentClass,
                                   val abilityEnabled: Collection<LogicalAbility> = listOf(),
                                   // attributes are applied at beginning of combat, with "applied by" tooltip
                                   val attributesApplied: Collection<LogicalCharacterAttribute> = listOf(),
                                   val effectToApply: LogicalAbilityEffect? = null,
                                   val uuid: UUID = UUID.randomUUID(),
                                   val infiniteQuantity: Boolean = false,
                                   val protoActor: ProtoActor = SuperimposedTilemaps.weaponImageNumber("1"))