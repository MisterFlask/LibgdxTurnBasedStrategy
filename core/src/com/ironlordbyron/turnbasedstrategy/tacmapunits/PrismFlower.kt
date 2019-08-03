package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


@SpawnableUnitTemplate("PRISM_FLOWER")
public fun PrismFlower(): TacMapUnitTemplate{
    return TacMapUnitTemplate(
            0,
            SuperimposedTilemaps.plantImageNumber("0"),
            "Prism Flower",
            listOf(PrismFlowerAttack()),
            listOf(),
            listOf(),
            EnemyAiType.IMMOBILE_UNIT,
            listOf(),
            maxHealth = 5,
            possibleRandomizedIntents = listOf(IntentType.ATTACK),
            templateId = "PRISM_FLOWER"
            )
}

public fun PrismFlowerAttack() : LogicalAbility{
    return LogicalAbility("Laser", AbilitySpeed.ENDS_TURN, 0, SuperimposedTilemaps.toDefaultProtoActor(), damage = 1,
            description = "Fires a laser beam in any direction",
            rangeStyle = RangeStyle.Linear(null),
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            requiresTarget = true,
            projectileActor = DataDrivenOnePageAnimation.FIREBALL,
            landingActor = null,
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY
        )
}