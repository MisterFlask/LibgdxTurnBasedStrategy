package com.ironlordbyron.turnbasedstrategy.common.abilities

object StandardAbilities{
    val MeleeAttack = LogicalAbility("Attack",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = RangeValue.MeleeAttack()
            );
}