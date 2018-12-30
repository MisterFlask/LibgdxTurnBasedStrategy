package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplateKeys

object StandardAbilities{

    val MeleeAttack = LogicalAbility("Stab",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 2,
            description = "A melee attack.  Ends the turn.",
            abilityClass = AbilityClass.TARGETED_ABILITY,
            requiredTargetType = RequiredTargetType.ENEMY_ONLY
            );
    val RangedAttackThatLightsStuffOnFire = LogicalAbility("Torch",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 5,
            description = "A ranged attack that lights stuff on fire.  Ends the turn.",
            abilityClass = AbilityClass.TARGETED_ABILITY,
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            abilityEffects = listOf(LogicalAbilityEffect.LightsTileOnFire())
    );
    val SpawnUnit = LogicalAbility("Spawn Unit",
            AbilitySpeed.ENDS_TURN,
            damage = null,
            range = 2,
            description = "Spawns an enemy unit at targeted location.",
            abilityClass = AbilityClass.TARGETED_ABILITY,
            requiredTargetType = RequiredTargetType.NO_CHARACTER_AT_LOCATION,
            abilityEffects = listOf(LogicalAbilityEffect.SpawnsUnit(TacMapUnitTemplateKeys.DEFAULT_ENEMY_UNIT)))
}