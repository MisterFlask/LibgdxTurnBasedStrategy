package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.Tags
import com.ironlordbyron.turnbasedstrategy.common.abilities.specific.GuardAbility
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate

@SpawnableUnitTemplate("DEFAULT_FRIENDLY_UNIT")
fun DefaultFriendlyUnit(): TacMapUnitTemplate {
    return TacMapUnitTemplate(8,
            TacMapUnitTemplate._default_sit.copy(textureId = "6"),
            templateName = "Conscript",
            abilities = listOf(GuardAbility),
            tags = Tags(isSpawnableEnemy = false))
}