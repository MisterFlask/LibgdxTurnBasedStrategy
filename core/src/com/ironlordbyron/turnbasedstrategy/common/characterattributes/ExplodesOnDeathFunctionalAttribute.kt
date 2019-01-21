package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor

class ExplodesOnDeathFunctionalAttribute(val radius: Int,
                                         val damage: Int,
                                         val entitySpawner: EntitySpawner,
                                         val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                         val protoActor: ProtoActor = DataDrivenOnePageAnimation.EXPLODE,
                                         val damageOperator: DamageOperator,
                                         val tacticalMapState: TacticalMapState) : FunctionalCharacterAttribute{
    override fun onDeath(thisCharacter: LogicalCharacter) {
        val locationsForExplosion = tacticalMapAlgorithms.getWalkableTileLocationsUpToNAway(n = radius, origin = thisCharacter.tileLocation, tileIsValidAlgorithm = AlwaysValid(),
               character = thisCharacter)
        val explosions = locationsForExplosion.map{
            EntitySpawner.SpawnEntityParams(
                    protoActor,
                    it,
                    AnimatedImageParams.RUN_ONCE_AFTER_DELAY)}
        entitySpawner.spawnEntitiesAtTilesInSequenceForTempAnimation(explosions)
        val charactersAtTiles = tacticalMapState.listOfCharacters.filter{it.tileLocation in locationsForExplosion}
        for (character in charactersAtTiles){
            damageOperator.damageCharacter(character, damage)
        }
    }
}
