package com.ironlordbyron.turnbasedstrategy.missiongen

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplateTags
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider

public class MissionUnitTemplateDecider{
    val unitTemplateRegistrar by LazyInject(UnitTemplateRegistrar::class.java)

    /**
     * Assumes we ALWAYS want a master organ and shielding organ.  Chooses randomly among other possible organs.
     */
    private fun getOrgansToBeUsedInMission(organsToUse: Int) : Collection<TacMapUnitTemplate>{
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

    private fun getMobsToBeUsedForOrganGuardianship(mobsToUse: Int): List<TacMapUnitTemplate> {
        val mobs = unitTemplateRegistrar.unitTemplates.filter{!it.tags.contains(SpawnableUnitTemplateTags.ORGAN)}
        val mobsDeck = mobs.repeat(3).toMutableList()
        mobsDeck.shuffle()
        return mobsDeck.take(mobsToUse).map{it.spawn()}
    }

    val tiledMapProvider by LazyInject(TileMapProvider::class.java)

    fun createUnitsAndOrgans(): Collection<UnitSpawnParameter>{
        val returnedUnitSpawns = ArrayList<UnitSpawnParameter>()
        var zones = tiledMapProvider.getDiscreteZones()
        zones = zones.shuffled()
        val organQueue = getOrgansToBeUsedInMission(5).toMutableList()
        for (zone in zones){
            val mobs = getMobsToBeUsedForOrganGuardianship(4)
            val organ = organQueue.pop()
            val tilesInZone = zone.tiles.shuffled().toList()
            mobs.forEachIndexed{
                i, template -> returnedUnitSpawns.add(UnitSpawnParameter(tilesInZone.get(i), template))
            }
            returnedUnitSpawns.add(UnitSpawnParameter(tilesInZone.last(), organ))
        }
        return returnedUnitSpawns
    }
}
data class UnitSpawnParameter(val tile: TileLocation, val tacMapUnitTemplate: TacMapUnitTemplate)
data class TileZone(val tiles: Collection<TileLocation>)

fun<T> Collection<T>.repeat(n: Int) : Collection<T>{
    val l = ArrayList<T>()
    for (i in 0 .. n){
        l.addAll(this)
    }
    return l
}

fun<T> MutableList<T>.pop(): T {
    val last = this.last()
    this.removeAt(this.lastIndex)
    return last
}