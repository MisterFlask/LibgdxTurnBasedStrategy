package com.ironlordbyron.turnbasedstrategy.tacmapunits.classes.nonclassabilities

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.entity
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.tacmapunits.actionManager
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WarpingInPortalTileProtoEntity

fun startingEgressSpellbook(): LogicalEquipment {
    return LogicalEquipment("Tome of Egress",
            EquipmentClass.SPELLBOOK,
            abilityEnabled = listOf(openEgressPortal())
    )
}

fun openEgressPortal(): LogicalAbility {
    return LogicalAbility("Open Egress Portal",
            speed = AbilitySpeed.ONE_ACTION,
            damageStyle = SimpleDamageStyle(0),
            rangeStyle = RangeStyle.Simple(4),
            abilityUsageTileFilter = NoTileEntityAllowedFilter(),
            description = "Creates a portal through which you can escape.",
            requiredTargetType = RequiredTargetType.ANY,
            requiresTarget = true,
            inflictsStatusAffect = listOf(),
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            range = -1,
            landingActor = null,
            projectileActor = null,
            mpCost = 2,
            abilityEffects = listOf(EgressEffect())
            )
}

class EgressEffect : LogicalAbilityEffect {
    override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
        actionManager.createTileEntity(WarpingInPortalTileProtoEntity(), tileLocationTargeted)
    }
}

interface TileFilter{
    fun tileIsValid(tileLocation: TileLocation): Boolean
}

class NullTileFilter(): TileFilter{
    override fun tileIsValid(tileLocation: TileLocation): Boolean {
        return true
    }

}

class NoTileEntityAllowedFilter(): TileFilter{
    override fun tileIsValid(tileLocation: TileLocation): Boolean {
        return tileLocation.entity() == null
    }

}