package com.ironlordbyron.turnbasedstrategy.tacmapunits.classes

import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptInput
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilitySpeed
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


class CommissarClass() : TacMapUnitClass("Commissar",
        "Keeps a melter pistol as a secondary weapon.  Has exactly one shot.  Grants the 'Motivate' ability.",
        EquipmentClass.PISTOL,
        8,
        1,
        6,
        startingSecondaryWeapon = startingMelterPistol(),
        protoActor = SuperimposedTilemaps.playerImageNumber("25")
)

fun startingMelterPistol(): LogicalEquipment {
    return LogicalEquipment("Commissar's Pistol", EquipmentClass.PISTOL,
            listOf(motivateAbility(), melterPistolShot()))
}

fun melterPistolShot(): LogicalAbility {
    return LogicalAbility("Melter Pistol Shot", AbilitySpeed.ONE_ACTION, 5, null, 1,
            null, "Increases movement rate by 3 and damage by 2 for the turn.", AbilityClass.TARGETED_ATTACK_ABILITY,
            allowsTargetingSelf = false, landingActor = null, projectileActor = null)
}

fun motivateAbility(): LogicalAbility {
    return LogicalAbility("'Motivate'", AbilitySpeed.ONE_ACTION, 5, null, 2,
            null, "Increases movement rate by 3 and damage by 2 for the turn.", AbilityClass.TARGETED_ATTACK_ABILITY,
            allowsTargetingSelf = false, landingActor = null, projectileActor = null, inflictsStatusAffect = listOf(MotivatedStatusEffect()))
}

class MotivatedStatusEffect() : LogicalCharacterAttribute("\"Motivated\"", SuperimposedTilemaps.toDefaultProtoActor(),
        false, false, {"Increases movement rate by 3 and damage by 2 for the turn."},
        statusEffect = false, stackable = false, id = "MOTIVATED") {
    override fun applyDamageModsAsAggressor(damageAttemptInput: DamageAttemptInput, params: FunctionalEffectParameters): DamageAttemptInput {
        return damageAttemptInput.copy(damage = damageAttemptInput.damage + 2)
    }

    override fun getMovementModifier(params: FunctionalEffectParameters): Int {
        return 2
    }
}