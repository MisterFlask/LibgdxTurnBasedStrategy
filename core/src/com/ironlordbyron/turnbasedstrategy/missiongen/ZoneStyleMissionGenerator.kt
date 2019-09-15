package com.ironlordbyron.turnbasedstrategy.missiongen

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.AdrenalGlands
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.SleepingGuardian
import com.ironlordbyron.turnbasedstrategy.common.wrappers.BattleGoal
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplateTags
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider

/**
 * Accepts a tac map as input (required to have Zone object layer)
 * Outputs list of units and locations of the same
 * Delegates individual zoning logic to mission goals
 */
public class ZoneStyleMissionGenerator{
    val unitTemplateRegistrar by LazyInject(UnitTemplateRegistrar::class.java)
    val levelAppropriateMinionGenerator by LazyInject(LevelAppropriateMinionGenerator::class.java)

    /**
     * Assumes we ALWAYS want a master organ and shielding organ.  Chooses randomly among other possible organs.
     */
    private fun getOrgansToBeUsedInMission(organsToUse: Int) : Collection<TacMapUnitTemplate>{
        val organs = unitTemplateRegistrar.unitTemplates.filter{it.compileTimeTags.contains(SpawnableUnitTemplateTags.ORGAN)}
                .toMutableList()
        val masterOrgan = organs.first{it.id == "MASTER_ORGAN"}
        organs.remove(masterOrgan)
        organs.shuffle()
        val chosen = organs.take(organsToUse).toMutableList()
        return chosen.map{it.spawn()}
    }

    private fun getMobsToBeUsedForOrganGuardianship(mobsToUse: Int): List<TacMapUnitTemplate> {
        val mobs = levelAppropriateMinionGenerator.getGenericMinions(mobsToUse)
        return mobs.take(mobsToUse)
    }

    val tiledMapProvider by LazyInject(TileMapProvider::class.java)

    fun createUnitsAndOrganGenerationParameters(battleGoals: Collection<BattleGoal>): Collection<UnitSpawnParameter>{
        val returnedUnitSpawns = ArrayList<UnitSpawnParameter>()
        val bespokeZones = battleGoals.flatMap{it.getRequiredZoneCreationParameters()}.toMutableList()
        var zones = tiledMapProvider.getDiscreteZones()
        zones = zones.shuffled()
        val nonObjectiveOrganQueue = getOrgansToBeUsedInMission(2).toMutableList()
        for (zone in zones){
            val tilesInZone = zone.tiles.shuffled().toList()

            if (bespokeZones.isNotEmpty()){
                val bespokeZone = bespokeZones.pop()
                val mobs = bespokeZone!!.unitSpawnParams
                 mobs.forEachIndexed{ i, mob ->
                    val unitSpawn = UnitSpawnParameter(tilesInZone.get(i), mob)
                    returnedUnitSpawns.add(unitSpawn)
                }
                continue // avoiding typical zone spawning
            }
            // default spawning logic
            val mobs = getMobsToBeUsedForOrganGuardianship(4)
            val organ = nonObjectiveOrganQueue.pop()
            if (organ == null){
                continue
            }
            mobs.forEachIndexed{
                i, template -> returnedUnitSpawns.add(UnitSpawnParameter(tilesInZone.get(i), template, listOf(SleepingGuardian(organ.unitId))))
            }

            returnedUnitSpawns.add(UnitSpawnParameter(tilesInZone.last(), organ, listOf(AdrenalGlands())))
        }
        return returnedUnitSpawns
    }
}
data class UnitSpawnParameter(val tile: TileLocation,
                              val tacMapUnitTemplate: TacMapUnitTemplate,
                              val attrsToApply: Collection<LogicalCharacterAttribute> = listOf())
data class TileZone(val tiles: Collection<TileLocation>)

fun<T> Collection<T>.repeat(n: Int) : Collection<T>{
    val l = ArrayList<T>()
    for (i in 0 .. n){
        l.addAll(this)
    }
    return l
}

fun<T> MutableList<T>.pop(): T? {
    val last = this.lastOrNull()
    if (last == null) return null
    this.removeAt(this.lastIndex)
    return last
}