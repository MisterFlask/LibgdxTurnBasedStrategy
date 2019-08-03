package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

@SpawnableUnitTemplate("MASTER_ORGAN")
fun MasterOrgan() : TacMapUnitTemplate{
    return TacMapUnitTemplate(0,
            SuperimposedTilemaps.elementalImageNumber("34"),
            templateName = "Master Organ",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            startingAttributes = listOf(LogicalCharacterAttribute.MASTER_ORGAN,
                    LogicalCharacterAttribute.EXPLODES_ON_DEATH))
}

fun ExplodesOnDeath(): LogicalCharacterAttribute {
    return LogicalCharacterAttribute("Explodes On Death",
            LogicalCharacterAttribute._painterlyIcon,
            customEffects = listOf(ExplodesOnDeathFunctionalUnitEffect(4, 5)),
            description = {"Explodes on death, dealing 5 damage to everything in a 4-tile radius"})
}