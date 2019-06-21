package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate


@SpawnableUnitTemplate("WEAK_MINION")
fun WeakMinion() : TacMapUnitTemplate{
    return TacMapUnitTemplate(8,
            TacMapUnitTemplate._demonImg.copy(textureId = "7"),
            templateName = "Enemy",
            abilities = listOf(StandardAbilities.SlimeStrike))
}