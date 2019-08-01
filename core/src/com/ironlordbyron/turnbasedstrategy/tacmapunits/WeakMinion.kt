package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.goals.AttackMetaGoal
import com.ironlordbyron.turnbasedstrategy.ai.goals.ConquerCityMetagoal
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate

// TODO: This unit doesn't move correctly.  But why?  It appears to be the attack metagoal
@SpawnableUnitTemplate("WEAK_SLIME")
fun WeakSlime() : TacMapUnitTemplate{
    return TacMapUnitTemplate(8,
            TacMapUnitTemplate._demonImg.copy(textureId = "7"),
            templateName = "Weak Slime",
            abilities = listOf(StandardAbilities.RangedAttack),
            metagoal = AttackMetaGoal())
}

@SpawnableUnitTemplate("SIMPLE_RANGED_ENEMY")
fun SimpleRangedEnemy() : TacMapUnitTemplate{
    return TacMapUnitTemplate(3,
            TacMapUnitTemplate._demonImg.copy(textureId = "7"),
            templateName = "Firespitter",
            abilities = listOf(StandardAbilities.RangedAttack))
}
@SpawnableUnitTemplate("SIMPLE_CITY_CONQUERER")
fun SimpleCityConquerer() : TacMapUnitTemplate{
    return TacMapUnitTemplate(3,
            TacMapUnitTemplate._demonImg.copy(textureId = "7"),
            templateName = "Firespitter",
            abilities = listOf(StandardAbilities.RangedAttack),
            metagoal = ConquerCityMetagoal())
}