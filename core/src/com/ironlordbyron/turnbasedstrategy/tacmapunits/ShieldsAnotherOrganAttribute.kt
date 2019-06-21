package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import java.util.*


/**
 * NOTE:  This IS NOT allowed to track state directly.  All state must be tracked by the Logical* classes.
 * Functional attributes are ONLY for algorithms.
 */
public class ShieldsAnotherOrganFunctionalAttribute(var thisShieldsCharacter: UUID? = null,
                                                    var lineActorId: UUID? = null,
                                                    var shieldActorId: UUID? = null)  : FunctionalAttributeEffect() {

    val actionManager: ActionManager by lazy {
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val specialEffectManager: SpecialEffectManager by lazy{
        GameModuleInjector.generateInstance(SpecialEffectManager::class.java)
    }
    val transientEntityTracker: TransientEntityTracker by lazy{
        GameModuleInjector.generateInstance(TransientEntityTracker::class.java)
    }

    override fun onDeath(params: FunctionalEffectParameters) {

        if (shieldActorId != null) {
            actionManager.despawnEntityInSequence(transientEntityTracker.retrieveActorByUuid(shieldActorId!!)!!)
        }
        if (lineActorId != null) {
            actionManager.destroySpecialEffectInSequence(lineActorId!!, params.thisCharacter.actor)
        }
    }

    override fun onInitialization(params: FunctionalEffectParameters) {
        val thisCharacter=  params.thisCharacter
        val masterOrgan = getCharacterWithMasterAttribute()
        if (masterOrgan != null) {

            val characterChosen = masterOrgan.id
            thisShieldsCharacter = characterChosen

            val shieldActor = actionManager.spawnEntityAtTileInSequence(
                    DataDrivenOnePageAnimation.RED_SHIELD_ACTOR,
                    masterOrgan.tileLocation)
            val uuid = transientEntityTracker.insertActor(shieldActor)

            shieldActorId = uuid
            val line = specialEffectManager.generateLaserEffect(thisCharacter.actor,
                    masterOrgan.actor)
            lineActorId = line.guid
            transientEntityTracker.insertLine(line)

        }
    }

    private fun getCharacterWithMasterAttribute(): LogicalCharacter? {
        return tacticalMapState
                .listOfCharacters
                .filter { character -> character.getAttributes().any { it.logicalAttribute.masterOrgan } }
                .firstOrNull()
    }
}