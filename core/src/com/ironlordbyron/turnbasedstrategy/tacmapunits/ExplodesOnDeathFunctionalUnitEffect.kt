package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.common.AlwaysValid
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject


class ExplodesOnDeathFunctionalUnitEffect @Inject constructor (val radius: Int, val damage: Int
) : FunctionalAttributeEffect(){
    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val tacticalMapAlgorithms: TacticalMapAlgorithms by lazy{
        GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
    }
    val damageOperator: DamageOperator by lazy{
        GameModuleInjector.generateInstance(DamageOperator::class.java)
    }
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }

    val protoActor: ProtoActor = DataDrivenOnePageAnimation.EXPLODE

    override fun onDeath(functionalEffectParameters: FunctionalEffectParameters) {
        val locationsForExplosion = tacticalMapAlgorithms.getWalkableTileLocationsUpToNAway(n = this.radius, origin = functionalEffectParameters.thisCharacter.tileLocation, tileIsValidAlgorithm = AlwaysValid(),
                character = functionalEffectParameters.thisCharacter)
        val explosions = locationsForExplosion.map{
            ActionManager.SpawnEntityParams(
                    protoActor,
                    it,
                    AnimatedImageParams.RUN_ONCE_AFTER_DELAY)}
        actionManager.spawnEntitiesAtTilesInSequenceForTempAnimation(explosions)
        val charactersAtTiles = tacticalMapState.listOfCharacters.filter{it.tileLocation in locationsForExplosion}
        for (character in charactersAtTiles){
            damageOperator.damageCharacter(character, this.damage, null, functionalEffectParameters.thisCharacter)
        }
    }

}