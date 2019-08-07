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
            1, null, null, 1, "Capable of a sweeping AoE attack.",
            AbilityClass.TARGETED_ATTACK_ABILITY, false, RequiredTargetType.ENEMY_ONLY, listOf(),
            null, null, areaOfEffect = SweepAoe())
}


/**
 * Should be something like
 * o o x
 * o # x
 * o o x
 */
class SweepAoe : AreaOfEffect {
    override fun getTilesAffected(tileLocationTargeted: TileLocation, characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment): Collection<TileLocation> {
        // each tile to the left and right of whatever tile was targeted, perpendicular to the direction of the character.
        val diff = characterUsing.tileLocation - tileLocationTargeted
        val nearby =
                if (diff.x == 0) listOf(characterUsing.tileLocation + TileLocation(1, 0), characterUsing.tileLocation + TileLocation(-1, 0))
                else  listOf(characterUsing.tileLocation + TileLocation(0, 1), characterUsing.tileLocation + TileLocation(0, -1))
        return nearby + characterUsing.tileLocation
    }

}
