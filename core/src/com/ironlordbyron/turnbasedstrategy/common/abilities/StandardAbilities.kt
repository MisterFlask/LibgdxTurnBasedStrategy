package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ImageIcon

object StandardAbilities{

    val SwordSlashAttack = LogicalAbility("Stab",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 2,
            description = "A melee attack.  Ends the turn.",
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            projectileActor = null,
            landingActor = DataDrivenOnePageAnimation.CLAWSLASH,
            attackSprite = ImageIcon(ImageIcon._PAINTERLY_FOLDER, "slice-acid-1.png")
            );
    val SlimeStrike = LogicalAbility("Slime strike",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 2,
            description = "Slimes the target, reducing speed.",
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            inflictsStatusAffect = listOf(LogicalCharacterAttribute.SLIMED),
            landingActor = DataDrivenOnePageAnimation.CLAWSLASH,
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            projectileActor = null)
    val FreeAimingFireball = LogicalAbility("Torch (free aiming)",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 5,
            description = "A ranged attack that lights stuff on fire.  Ends the turn.",
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            requiredTargetType = RequiredTargetType.ANY_CHARACTER,
            abilityEffects = listOf(),
            projectileActor = DataDrivenOnePageAnimation.FIREBALL,
            landingActor = DataDrivenOnePageAnimation.EXPLODE,
            inflictsStatusAffect = listOf(LogicalCharacterAttribute.ON_FIRE),
            cooldownTurns = 4
    );
    val Beatdown = LogicalAbility("Beatdown",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 2,
            description = "Stuns the targeted enemy for the turn.",
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            inflictsStatusAffect = listOf(LogicalCharacterAttribute.STUNNED),
            landingActor = DataDrivenOnePageAnimation.CLAWSLASH,
            projectileActor = null
            )
    val RangedAttack = LogicalAbility("Torch",
            AbilitySpeed.ENDS_TURN,
            damage = 1,
            range = 5,
            rangeStyle = RangeStyle.Simple(5, 2),
            description = "A ranged attack that lights stuff on fire.  Ends the turn.",
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            abilityEffects = listOf(),
            projectileActor = DataDrivenOnePageAnimation.FIREBALL,
            landingActor = DataDrivenOnePageAnimation.EXPLODE,
            inflictsStatusAffect = listOf(LogicalCharacterAttribute.ON_FIRE)
    );
}