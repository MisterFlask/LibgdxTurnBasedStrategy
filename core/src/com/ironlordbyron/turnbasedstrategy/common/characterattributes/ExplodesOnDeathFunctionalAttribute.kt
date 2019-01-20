package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor

class ExplodesOnDeathFunctionalAttribute(val radius: Int,
                                         val damage: Int,
                                         val entitySpawner: EntitySpawner,
                                         val tacticalMapAlgorithms: TacticalMapAlgorithms) : FunctionalCharacterAttribute{
    override fun onDeath(thisCharacter: LogicalCharacter) {
        val locationsForExplosion = tacticalMapAlgorithms.getWalkableTileLocationsUpToNAway(n = radius, origin = thisCharacter.tileLocation, tileIsValidAlgorithm = AlwaysValid(),
               character = thisCharacter)
        val explosions = locationsForExplosion.map{
            EntitySpawner.SpawnEntityParams(
                    DataDrivenOnePageAnimation.EXPLODE,
                    it,
                    AnimatedImageParams.RUN_ONCE_AFTER_DELAY)} //RUN ONCE AFTER DELAY IS THE PROBLEM CHILD
        entitySpawner.spawnEntitiesAtTilesInSequence(explosions)
    }
}
