package com.ironlordbyron.turnbasedstrategy.common.abilities.specific

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.*

public fun DefendAbility() = LogicalAbility(
        "Defend", AbilitySpeed.ONE_ACTION, -1, null, null, null,
        "Defend against incoming damage for the next turn", AbilityClass.TARGETED_ATTACK_ABILITY, true,
        RequiredTargetType.ALLY_ONLY, // TODO: Require target of self
        abilityEffects = listOf(DefendAbilityEffect(1)),
        landingActor = null, projectileActor = null)

class DefendAbilityEffect(val magnitude: Int) : LogicalAbilityEffect {
    override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
        // todo: Add this to action manager
        characterUsing.tacMapUnit.block += magnitude + characterUsing.tacMapUnit.dexterity
    }
}
