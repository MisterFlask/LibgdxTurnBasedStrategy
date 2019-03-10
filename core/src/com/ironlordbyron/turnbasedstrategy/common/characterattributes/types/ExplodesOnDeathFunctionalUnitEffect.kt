package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.AlwaysValid
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttributeTrigger
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject
import javax.inject.Singleton


data class ExplodesOnDeath(val radius: Int, val damage: Int) : LogicalUnitEffect{
    override fun toEntry() : Pair<String, Any>{
        return "EXPLODES_ON_DEATH" to this
    }
}

@Autoinjectable
@Singleton
class ExplodesOnDeathFunctionalUnitEffect @Inject constructor (val entitySpawner: EntitySpawner,
                                                               val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                               val damageOperator: DamageOperator,
                                                               val tacticalMapState: TacticalMapState) : FunctionalUnitEffect<ExplodesOnDeath>{

    val protoActor: ProtoActor = DataDrivenOnePageAnimation.EXPLODE
    override val id: String = "EXPLODES_ON_DEATH"
    override val clazz = ExplodesOnDeath::class.java
    override fun onDeath(logicalAttr: ExplodesOnDeath, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute) {
        val locationsForExplosion = tacticalMapAlgorithms.getWalkableTileLocationsUpToNAway(n = logicalAttr.radius, origin = thisCharacter.tileLocation, tileIsValidAlgorithm = AlwaysValid(),
                character = thisCharacter)
        val explosions = locationsForExplosion.map{
            EntitySpawner.SpawnEntityParams(
                    protoActor,
                    it,
                    AnimatedImageParams.RUN_ONCE_AFTER_DELAY)}
        entitySpawner.spawnEntitiesAtTilesInSequenceForTempAnimation(explosions)
        val charactersAtTiles = tacticalMapState.listOfCharacters.filter{it.tileLocation in locationsForExplosion}
        for (character in charactersAtTiles){
            damageOperator.damageCharacter(character, logicalAttr.damage, null)
        }
    }

}