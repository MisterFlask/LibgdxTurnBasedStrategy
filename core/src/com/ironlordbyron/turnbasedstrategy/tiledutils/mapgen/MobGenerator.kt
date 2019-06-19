package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.specific.SNOOZING
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// Responsible for deciding what monsters to put in a given room
public class MobGenerator @Inject constructor (val mobRegistrar: MobRegistrar,
                                               val actionManager: ActionManager,
                                               val attributeOperator: AttributeOperator){
    fun populateRooms(rooms : Collection<MapRoom>, scenarioParams: ScenarioParams){
        val mobGenParams = scenarioParams.mobGenerationParams!!
        var difficultyLeft = mobGenParams.totalDifficultyAllowed
        val mobs = ArrayList<MobGroup>()
        for (i in 0 .. mobGenParams.numberMobsToGenerate){
            val nextMob = nextMobGroupToGenerate(difficultyLeft / rooms.size)
            mobs.add(nextMob)
        }
        for (room in rooms.shuffled()){
            if (mobs.isEmpty()){
                break
            }
            val nextMob = mobs.last()
            mobs.removeAt(mobs.size - 1)
            populateMobGroupInRoom(nextMob, room)
        }
    }

    private fun populateMobInRoom(nextMob: TacMapUnitTemplate, room: MapRoom) {
        val tile = room.tiles.first()
        actionManager.addCharacterToTileFromTemplate(nextMob, tile, false)
    }

    private fun populateMobGroupInRoom(nextMobGroup: MobGroup, room: MapRoom){
        val tilesShuffled = room.tiles.shuffled()
        nextMobGroup.mobGroupTemplate.units.forEachIndexed { index, tacMapUnitTemplate ->
            val logicalCharacterReturned = actionManager.addCharacterToTileFromTemplate(tacMapUnitTemplate, tilesShuffled[index], false)
            attributeOperator.applyAttribute(logicalCharacterReturned, SNOOZING)
        }
    }

    fun nextMobGroupToGenerate(targetDifficulty: Int) : MobGroup {
        return mobRegistrar.getMobGroupWithinDifficultyRange(IntRange(targetDifficulty -5, targetDifficulty + 10))
    }

    fun nextMobToGenerate(targetDifficulty: Int): TacMapUnitTemplate {
        return mobRegistrar.getMobWithinDifficultyRange(IntRange(targetDifficulty -5 , targetDifficulty + 10))
    }
}

@Autoinjectable
@Singleton
public class MobRegistrar(){
    val listOfTemplatesToCopy = ArrayList<TacMapUnitTemplate>()
    val listOfMobsGroupTemplates = ArrayList<MobGroupTemplate>()
    fun registerTemplate(tacMapUnitTemplate: TacMapUnitTemplate){
        listOfTemplatesToCopy.add(tacMapUnitTemplate)
    }

    fun getMobWithinDifficultyRange(range: IntRange) : TacMapUnitTemplate{
        listOfTemplatesToCopy.shuffle()
        return listOfTemplatesToCopy.first{it.difficulty in range}.copy()
    }

    fun getMobGroupWithinDifficultyRange(range: IntRange) : MobGroup {
        listOfMobsGroupTemplates.shuffle()
        return listOfMobsGroupTemplates.first{it.difficulty() in range}.toMobGroup()
    }

    fun registerMobGroupTemplate(mobGroupTemplate: MobGroupTemplate){
        listOfMobsGroupTemplates.add(mobGroupTemplate)
    }

    //// Individual mob groups

    val standardMobGroup = MobGroupTemplate(
            name = "Standard Mob Group",
            tags = listOf("demo"),
            units = listOf(
            TacMapUnitTemplate.DEFAULT_ENEMY_UNIT,
            TacMapUnitTemplate.DEFAULT_ENEMY_UNIT,
            TacMapUnitTemplate.DEFAULT_ENEMY_UNIT))
    init{
        registerMobGroupTemplate(standardMobGroup)
    }
}

data class MobGroup(val mobGroupTemplate : MobGroupTemplate){
    val id = UUID.randomUUID()
}
data class MobGroupTemplate(val name: String,
                            val tags: Collection<String> = listOf(),
                            val units: Collection<TacMapUnitTemplate>){
    fun toMobGroup(): MobGroup {
        return MobGroup(mobGroupTemplate = this)
    }
    fun difficulty() : Int {
        return units.map{it.difficulty}.sum()
    }
}

fun TacMapUnitTemplate.register() : TacMapUnitTemplate{
    val registrar = GameModuleInjector.generateInstance(MobRegistrar::class.java)
    registrar.registerTemplate(this)
    return this
}
