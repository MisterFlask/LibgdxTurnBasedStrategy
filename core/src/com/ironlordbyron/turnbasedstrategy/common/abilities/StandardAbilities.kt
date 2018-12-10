package com.ironlordbyron.turnbasedstrategy.common.abilities

object StandardAbilities{
    val MeleeAttack = LogicalAbility("Attack",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 2,
            description = "A melee attack.  Ends the turn.",
            abilityClass = AbilityClass.TARGETED_ABILITY
            );
}