package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.PainterlyIcons
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

@SpawnableUnitTemplate("FLAME_TOWER")
public fun FlameTower(): TacMapUnitTemplate {
    return TacMapUnitTemplate(
            0,
            SuperimposedTilemaps.elementalImageNumber("2"),
            "Flame Tower",
            listOf(FlameTowerAttack()),
            listOf(),
            listOf(),
            EnemyAiType.IMMOBILE_UNIT,
            listOf(),
            maxHealth = 5,
            possibleRandomizedIntents = listOf(IntentType.ATTACK),
            templateId = "FLAME_TOWER"
    )
}

public fun FlameTowerAttack() : LogicalAbility{
    return LogicalAbility("Laser", AbilitySpeed.ENDS_TURN,
            0,
            PainterlyIcons.FIRE_ARROWS.toProtoActor(1),
            damage = 2,
            description = "Fires a gout of flame",
            rangeStyle = RangeStyle.Simple(6),
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            requiresTarget = true,
            projectileActor = DataDrivenOnePageAnimation.FIREBALL,
            landingActor = null,
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY

    )
}
