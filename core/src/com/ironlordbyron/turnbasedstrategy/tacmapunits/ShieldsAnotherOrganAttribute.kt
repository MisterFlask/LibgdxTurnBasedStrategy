package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptInput
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.randomElement
import com.ironlordbyron.turnbasedstrategy.toCharacter
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.PainterlyIcons
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import java.util.*

/**
 * NOTE:  This IS NOT allowed to track state directly.  All state must be tracked by the Logical* classes.
 * Functional attributes are ONLY for algorithms.
 */
public class ShieldsAnotherOrganFunctionalAttribute(val tacMapUnitTemplateIdToShield: String? = "MASTER_ORGAN")
    : LogicalCharacterAttribute(
        "Shields Organ",
        PainterlyIcons.PROTECT_SKY.toProtoActor(3),
        description = {"Shields an organ from all damage"}) {

    var thisShieldsCharacter: UUID? = null
    var lineActorId: UUID? = null
    var shieldActorId: UUID? = null
    val actionManager: ActionManager by lazy {
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val tacticalMapState: TacticalMapState by lazy {
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val specialEffectManager: SpecialEffectManager by lazy {
        GameModuleInjector.generateInstance(SpecialEffectManager::class.java)
    }
    val transientEntityTracker: TransientEntityTracker by lazy {
        GameModuleInjector.generateInstance(TransientEntityTracker::class.java)
    }
    val attributeActionManager by LazyInject(AttributeActionManager::class.java)

    override fun onDeath(params: FunctionalEffectParameters) {

        if (shieldActorId != null) {
            actionManager.despawnEntityInSequence(transientEntityTracker.retrieveActorByUuid(shieldActorId!!)!!)
        }
        if (lineActorId != null) {
            actionManager.destroySpecialEffectInSequence(lineActorId!!, params.thisCharacter.actor)
        }
        if (thisShieldsCharacter != null){
            attributeActionManager.unapplyAttribute(thisShieldsCharacter!!.toCharacter(), ImpenetrableShieldingAttribute())
        }
    }

    override fun onInitialization(params: FunctionalEffectParameters) {
        val thisCharacter = params.thisCharacter
        val organToBeShielded = getCharacterWithAppropriateAttribute()
        if (organToBeShielded != null) {
            val characterChosen = organToBeShielded.id
            thisShieldsCharacter = characterChosen

            val shieldActor = actionManager.spawnEntityAtTileInSequence(
                    DataDrivenOnePageAnimation.RED_SHIELD_ACTOR,
                    organToBeShielded.tileLocation)
            val uuid = transientEntityTracker.insertActor(shieldActor)

            shieldActorId = uuid
            val line = specialEffectManager.generateLaserEffect(thisCharacter.actor,
                    organToBeShielded.actor)
            lineActorId = line.guid
            transientEntityTracker.insertLine(line)
            // TODO
            //if (organToBeShielded.hasAttribute(ImpenetrableShieldingAttribute())){
            //    throw IllegalStateException("Organ cannot have impenetrable shielding twice")
            //}
            attributeActionManager.applyAttribute(organToBeShielded, ImpenetrableShieldingAttribute())
        }
    }

    private fun getCharacterWithAppropriateAttribute(): LogicalCharacter? {
        if (tacMapUnitTemplateIdToShield == null){
            return tacticalMapState
                    .listOfCharacters
                    .filter{it.tacMapUnit.tags.isOrgan}
                    .filter{!it.hasAttribute(this)}
                    .randomElement()
        }
        return tacticalMapState
                .listOfCharacters
                .filter { it.tacMapUnit.templateId == tacMapUnitTemplateIdToShield }
                .filter{ it.getAttributes().all{attr -> attr.logicalAttribute !== this}}
                .firstOrNull()
    }
}

public class ImpenetrableShieldingAttribute(): LogicalCharacterAttribute(
        "Impenetrable Shielding",
        PainterlyIcons.PROTECT_SKY.toProtoActor(3),
        description = {"While the organ shielding this still lives, this can't be damaged."}

){
    override fun applyDamageModsAsVictim(damageAttemptInput: DamageAttemptInput, params: FunctionalEffectParameters): DamageAttemptInput {
        return damageAttemptInput.copy(damage = 0)
    }
}