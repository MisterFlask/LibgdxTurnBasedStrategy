package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import java.util.*


/**
 * NOTE:  This IS NOT allowed to track state directly.  All state must be tracked by the Logical* classes.
 * Functional attributes are ONLY for algorithms.
 */
public class ShieldsAnotherOrganFunctionalAttribute(val actionManager: ActionManager,
                                                    val tacticalMapState: TacticalMapState,
                                                    val specialEffectManager: SpecialEffectManager,
                                                    val logicalCharacterAttribute: LogicalCharacterAttribute,
                                                    val transientEntityTracker: TransientEntityTracker): FunctionalCharacterAttribute() {
    val thisShieldsCharacter: UUID? get() = logicalCharacterAttribute.shieldsAnotherOrgan!!.characterShieldedId

    val shieldActor: UUID? get() = logicalCharacterAttribute.shieldsAnotherOrgan!!._characterShieldActorId
    val lineEffect: UUID? get() = logicalCharacterAttribute.shieldsAnotherOrgan!!._lineActorId

    override fun onDeath(thisCharacter: LogicalCharacter) {
        val shieldActor = this.shieldActor
        if (shieldActor != null) {
            actionManager.despawnEntityInSequence(transientEntityTracker.retrieveActorByUuid(shieldActor)!!)
        }
        if (lineEffect != null) {
            actionManager.destroySpecialEffectInSequence(lineEffect!!, thisCharacter.actor)
        }
    }

    override fun onInitialization(thisCharacter: LogicalCharacter) {
        val logicalAttr = logicalCharacterAttribute.shieldsAnotherOrgan!!
        val masterOrgan = getCharacterWithMasterAttribute()
        if (masterOrgan != null) {

            val characterChosen = masterOrgan.id
            logicalAttr.characterShieldedId = characterChosen

            val shieldActor = actionManager.spawnEntityAtTileInSequence(
                    DataDrivenOnePageAnimation.RED_SHIELD_ACTOR,
                    masterOrgan.tileLocation)
            val uuid = transientEntityTracker.insertActor(shieldActor)

            logicalAttr._characterShieldActorId = uuid
            val line = specialEffectManager.generateLaserEffect(thisCharacter.actor,
                    masterOrgan.actor)
            logicalAttr._lineActorId = line.guid
            transientEntityTracker.insertLine(line)
            logicalCharacterAttribute.shieldsAnotherOrgan!!._characterShieldActorId
        }
    }

    private fun getCharacterWithMasterAttribute(): LogicalCharacter? {
        return tacticalMapState
                .listOfCharacters
                .filter { character -> character.getAttributes().any { it.logicalAttribute.masterOrgan } }
                .firstOrNull()
    }
}