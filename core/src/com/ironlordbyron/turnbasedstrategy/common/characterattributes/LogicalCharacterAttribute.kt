package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import com.ironlordbyron.turnbasedstrategy.view.animation.external.LineEffect
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import java.util.*
import javax.inject.Inject

/**
 * So, here's how this works: Each attribute corresponds to a visible icon for the player to interact with.
 * it may have ANY of the provided attributes, and new attributes should be added by .
 */
public data class LogicalCharacterAttribute(val name: String,
                                            val imageIcon: ProtoActor,
                                            val explodesOnDeath: LogicalCharacterAttributeTrigger.ExplodesOnDeath? = null,
                                            val shieldsAnotherOrgan: LogicalCharacterAttributeTrigger.ShieldsAnotherOrgan? = null,
                                            val masterOrgan: Boolean = false,
                                            val organ: Boolean = false,
                                            val description: (LogicalCharacterAttribute) -> String){
    companion object {
        val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        val EXPLODES_ON_DEATH = LogicalCharacterAttribute("Explodes On Death",
                _demonImg,
                LogicalCharacterAttributeTrigger.ExplodesOnDeath(3, 4),
                description = {"Explodes on death, dealing ${it.explodesOnDeath!!.damage} to everything in a ${it.explodesOnDeath!!.radius} radius"})
        val MASTER_ORGAN = LogicalCharacterAttribute("Master Organ",
                _demonImg.copy(textureId = "3"),
                masterOrgan= true,
                description = {"Master organ.  When destroyed, the fortress will begin sinking back into Hell."})
        val SHIELDS_ANOTHER_ORGAN = LogicalCharacterAttribute("Shields Organ",
                _demonImg.copy(textureId = "4"),
                shieldsAnotherOrgan = LogicalCharacterAttributeTrigger.ShieldsAnotherOrgan(),
                description = {"Shields an organ from all damage."})
    }
}

public class FunctionalCharacterAttributeFactory @Inject constructor (val entitySpawner: EntitySpawner,
                                                                      val tacticalMapState: TacticalMapState,
                                                                      val specialEffectManager: SpecialEffectManager,
                                                                      val transientEntityTracker: TransientEntityTracker){

    fun getFunctionalAttributesForCharacter(logicalCharacter: LogicalCharacter): List<FunctionalCharacterAttribute> {
        return logicalCharacter.attributes.flatMap{getFunctionalAttributesFromLogicalAttribute(it, logicalCharacter)}
    }

    fun getFunctionalAttributesFromLogicalAttribute(logicalAttribute: LogicalCharacterAttribute, character: LogicalCharacter) : Collection<FunctionalCharacterAttribute>{
        val attrsList = ArrayList<FunctionalCharacterAttribute>()
        if (logicalAttribute.shieldsAnotherOrgan != null){
            val funcAttr = ShieldsAnotherOrganFunctionalAttribute(entitySpawner, tacticalMapState, specialEffectManager, logicalAttribute,
                    transientEntityTracker)
            attrsList.add(funcAttr)
        }

        return attrsList
    }

}

public interface LogicalCharacterAttributeTrigger{
    // The below are Organ abilities
    data class ExplodesOnDeath(val radius: Int, val damage: Int) : LogicalCharacterAttributeTrigger
    data class ShieldsAnotherOrgan(var characterShieldedId: UUID? = null,
                                   var _characterShieldActorId: UUID? = null,// transient attribute
                                   var _lineActorId: UUID? = null // transient attribute
    ): LogicalCharacterAttributeTrigger
    class MasterOrgan: LogicalCharacterAttributeTrigger
    class Organ: LogicalCharacterAttributeTrigger
}


/**
 * NOTE:  This IS NOT allowed to track state directly.  All state must be tracked by the Logical* classes.
 * Functional attributes are ONLY for algorithms.
 */
public class ShieldsAnotherOrganFunctionalAttribute(val entitySpawner: EntitySpawner,
                                                    val tacticalMapState: TacticalMapState,
                                                    val specialEffectManager: SpecialEffectManager,
                                                    val logicalCharacterAttribute: LogicalCharacterAttribute,
                                                    val transientEntityTracker: TransientEntityTracker): FunctionalCharacterAttribute {
    val thisShieldsCharacter : UUID? get() = logicalCharacterAttribute.shieldsAnotherOrgan!!.characterShieldedId

    val shieldActor: UUID? get() =logicalCharacterAttribute.shieldsAnotherOrgan!!._characterShieldActorId
    val lineEffect: UUID? get() = logicalCharacterAttribute.shieldsAnotherOrgan!!._lineActorId

    override fun onDeath(thisCharacter: LogicalCharacter){
        val shieldActor = this.shieldActor
        if (shieldActor != null){
            entitySpawner.despawnEntityInSequence(transientEntityTracker.retrieveActorByUuid(shieldActor)!!)
        }
        if (lineEffect != null){
            entitySpawner.destroySpecialEffectInSequence(lineEffect!!, thisCharacter.actor)
        }
    }

    override fun onInitialization(thisCharacter: LogicalCharacter) {
        val logicalAttr = logicalCharacterAttribute.shieldsAnotherOrgan!!
        val masterOrgan = getCharacterWithMasterAttribute()
        if (masterOrgan != null){

            val characterChosen = masterOrgan.id
            logicalAttr.characterShieldedId  = characterChosen

            val shieldActor = entitySpawner.spawnEntityAtTileInSequence(
                    DataDrivenOnePageAnimation.RED_SHIELD_ACTOR,
                    masterOrgan.tileLocation)
            val uuid = transientEntityTracker.insertActor(shieldActor)

            logicalAttr._characterShieldActorId = uuid
            val line = specialEffectManager.generateLineEffect(thisCharacter.actor,
                    masterOrgan.actor)
            logicalAttr._lineActorId = line.guid
            transientEntityTracker.insertLine(line)
            logicalCharacterAttribute.shieldsAnotherOrgan!!._characterShieldActorId
        }
    }

    private fun getCharacterWithMasterAttribute(): LogicalCharacter? {
        return tacticalMapState
                .listOfCharacters
                .filter { character -> character.attributes.any { it.masterOrgan } }
                .firstOrNull()
    }


}

// These represent modifiers to characters that result in things happening on triggers.
public interface FunctionalCharacterAttribute{
    // defines a function to be run on death.
    fun onDeath(thisCharacter: LogicalCharacter){

    }

    // Defines a function to be run on the start of the enemy (as opposed to player) turn.
    fun onEnemyTurnStart(thisCharacter: LogicalCharacter){

    }

    // this is run after all items have been placed on the map, but BEFORE the first turn is taken.
    fun onInitialization(thisCharacter: LogicalCharacter){

    }
}