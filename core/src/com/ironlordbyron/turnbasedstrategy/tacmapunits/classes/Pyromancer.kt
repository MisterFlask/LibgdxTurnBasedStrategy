package com.ironlordbyron.turnbasedstrategy.tacmapunits.classes

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


class PyromancerClass() : TacMapUnitClass("Pyromancer",
        "Can set things on fire!",
        EquipmentClass.LONGSWORD,
        5,
        0,
        7,
        startingSecondaryWeapon = startingPyromancerSpellbook(),
        protoActor = SuperimposedTilemaps.playerImageNumber("27")
){
}

fun startingPyromancerSpellbook(): LogicalEquipment {
    return LogicalEquipment("Tome of Fire",
            EquipmentClass.SPELLBOOK,
            abilityEnabled = listOf(ignite())
    )
}

fun ignite(): LogicalAbility {
    return LogicalAbility("Ignite",
            speed = AbilitySpeed.ONE_ACTION,
            damageStyle = SimpleDamageStyle(1),
            rangeStyle = RangeStyle.Simple(4),
            description = "Damages an enemy and lights them on fire.",
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            requiresTarget = true,
            inflictsStatusAffect = listOf(LogicalCharacterAttribute.ON_FIRE),
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            range = -1,
            landingActor = null,
            projectileActor = null,
            mpCost = 1)
}
