package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject
import javax.inject.Singleton

// Responsible for deciding what monsters to put in a given room
public class MobGenerator @Inject constructor (val mobRegistrar: MobRegistrar,
                                               val entitySpawner: EntitySpawner){
    fun populateRooms(rooms : Collection<MapRoom>, scenarioParams: ScenarioParams){
        val mobGenParams = scenarioParams.mobGenerationParams!!
        var difficultyLeft = mobGenParams.totalDifficultyAllowed
        val mobs = ArrayList<TacMapUnitTemplate>()
        for (i in 0 .. mobGenParams.numberMobsToGenerate){
            val nextMob = nextMobToGenerate(difficultyLeft / rooms.size)
            mobs.add(nextMob)
        }
        for (room in rooms.shuffled()){
            if (mobs.isEmpty()){
                break
            }
            val nextMob = mobs.last()
            mobs.removeAt(mobs.size - 1)
            populateMobInRoom(nextMob, room)
        }
    }

    private fun populateMobInRoom(nextMob: TacMapUnitTemplate, room: MapRoom) {
        val tile = room.tiles.first()
        entitySpawner.addCharacterToTileFromTemplate(nextMob, tile, false)
    }

    fun nextMobToGenerate(targetDifficulty: Int): TacMapUnitTemplate {
        return mobRegistrar.getMobWithinDifficultyRange(IntRange(targetDifficulty -1 , targetDifficulty + 1))
    }
}

@Autoinjectable
@Singleton
public class MobRegistrar(){
    val listOfTemplatesToCopy = ArrayList<TacMapUnitTemplate>()

    fun registerTemplate(tacMapUnitTemplate: TacMapUnitTemplate){
        listOfTemplatesToCopy.add(tacMapUnitTemplate)
    }

    fun getMobWithinDifficultyRange(range: IntRange) : TacMapUnitTemplate{
        listOfTemplatesToCopy.shuffle()
        return listOfTemplatesToCopy.first{it.difficulty in range}.copy()
    }
}

fun TacMapUnitTemplate.register() : TacMapUnitTemplate{
    val registrar = GameModuleInjector.moduleInjector.getInstance(MobRegistrar::class.java)
    registrar.registerTemplate(this)
    return this
}
