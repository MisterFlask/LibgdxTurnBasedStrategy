package com.ironlordbyron.turnbasedstrategy.missiongen

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplateTags
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject

public class MissionUnitTemplateDecider{
    val unitTemplateRegistrar by LazyInject(UnitTemplateRegistrar::class.java)

    /**
     * Assumes we ALWAYS want a master organ and shielding organ.  Chooses randomly among other possible organs.
     */
    fun getOrgansToBeUsedInMission(organsToUse: Int) : Collection<TacMapUnitTemplate>{
        assert(organsToUse > 3)
        val organs = unitTemplateRegistrar.unitTemplates.filter{it.tags.contains(SpawnableUnitTemplateTags.ORGAN)}
                .toMutableList()
        val masterOrgan = organs.first{it.id == "MASTER_ORGAN"}
        val shieldingOrgan = organs.first{it.id == "SHIELDING_ORGAN"}
        organs.remove(masterOrgan)
        organs.remove(shieldingOrgan)
        organs.shuffle()
        val chosen = organs.take(organsToUse - 2).toMutableList()
        chosen.add(masterOrgan)
        chosen.add(shieldingOrgan)

        return chosen.map{it.spawn()}
    }

    fun getMobsToBeUsedOnMission(mobsToUse: Int): List<TacMapUnitTemplate> {

        val mobs = unitTemplateRegistrar.unitTemplates.filter{!it.tags.contains(SpawnableUnitTemplateTags.ORGAN)}

        val mobsDeck = mobs.repeat(3).toMutableList()
        mobsDeck.shuffle()
        return mobsDeck.take(mobsToUse).map{it.spawn()}
    }


}

fun<T> Collection<T>.repeat(n: Int) : Collection<T>{
    val l = ArrayList<T>()
    for (i in 0 .. n){
        l.addAll(this)
    }
    return l
}