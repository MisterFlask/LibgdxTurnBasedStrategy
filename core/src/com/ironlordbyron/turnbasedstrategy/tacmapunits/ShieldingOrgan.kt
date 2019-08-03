package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


@SpawnableUnitTemplate("SHIELDING_ORGAN")
fun ShieldingOrgan() : TacMapUnitTemplate{
    return TacMapUnitTemplate(0,
            SuperimposedTilemaps.elementalImageNumber("32"),
            templateName = "Shielding Organ",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            startingAttributes = listOf(LogicalCharacterAttribute.SHIELDS_ANOTHER_ORGAN,
                    LogicalCharacterAttribute.EXPLODES_ON_DEATH))
}