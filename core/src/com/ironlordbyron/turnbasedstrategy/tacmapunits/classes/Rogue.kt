package com.ironlordbyron.turnbasedstrategy.tacmapunits.classes

import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptInput
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilitySpeed
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.RequiredTargetType
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


class RogueClass() : TacMapUnitClass("Rogue",
        "Has the secondary weapon of Cloak & Dagger, which grants Concealment and the ability to sneak attack.",
        EquipmentClass.DAGGER,
        6,
        0,
        6,
        startingSecondaryWeapon = startingDagger()
){
    override fun createNewTacMapUnit(): TacMapUnitTemplate {

        return TacMapUnitTemplate.DEFAULT_ENEMY_UNIT
    }
}

fun startingDagger(): LogicalEquipment {
    return LogicalEquipment("Dagger",
            EquipmentClass.DAGGER,
            abilityEnabled = listOf(disappear())
    )
}

fun disappear(): LogicalAbility {
    return LogicalAbility("Disappear",
            speed = AbilitySpeed.ONE_ACTION,
            rangeStyle = SelfOnlyRangeStyle(),
            description = "Hides you from enemies until you take a non-movement action." +
                    "Your first attack will deal +3 damage.",
            requiredTargetType = RequiredTargetType.ALLY_ONLY,
            requiresTarget = false,
            inflictsStatusAffect = listOf(SkulkingStatusEffect(3)),
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            range = -1,
            landingActor = null,
            projectileActor = null,
            cooldownTurns = 5)
}

class SkulkingStatusEffect(val modifier: Int): LogicalCharacterAttribute("Skulking",
        id = "SKULKING",
        description = {"This character hides in the shadows.  If concealment is broken by an attack, it deals an extra $modifier damage."},
        stackable = false,
        imageIcon = SuperimposedTilemaps.toDefaultProtoActor()) {
    override fun applyDamageModsAsVictim(damageAttemptInput: DamageAttemptInput, params: FunctionalEffectParameters): DamageAttemptInput {
        if (!damageAttemptInput.simulation){
            this.removeThisAttribute(this.id, params.thisCharacter)
        }
        return damageAttemptInput.copy(damage = damageAttemptInput.damage + modifier)
    }

}