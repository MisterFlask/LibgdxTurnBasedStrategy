package com.ironlordbyron.turnbasedstrategy.common.abilities

object StandardAbilities{
    val MeleeAttack = LogicalAbility("Attack",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 2,
            description = "A melee attack.  Ends the turn.",
            abilityClass = AbilityClass.TARGETED_ABILITY,
            requiredTargetType = RequiredTargetType.ENEMY_ONLY
            );
    val spawnUnit = LogicalAbility("Spawn Unit",
            AbilitySpeed.ENDS_TURN,
            damage = null,
            range = 1,
            description = "Spawns an enemy unit at targeted location.",
            abilityClass = AbilityClass.TARGETED_ABILITY,
            requiredTargetType = RequiredTargetType.ANY)
}