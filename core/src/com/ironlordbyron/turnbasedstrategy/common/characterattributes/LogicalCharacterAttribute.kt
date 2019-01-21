package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ImageIcon
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
                                            val description: (LogicalCharacterAttribute) -> String,
                                            val statusEffect: Boolean = false,
                                            val damageOverTime: LogicalCharacterAttributeTrigger.DamageOverTimeAttribute? = null){
    companion object {
        val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        val _painterlyIcon = ImageIcon(ImageIcon.PAINTERLY_FOLDER, "fire-arrows-1.png")
        val EXPLODES_ON_DEATH = LogicalCharacterAttribute("Explodes On Death",
                _painterlyIcon,
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
        val UPGRADES_TROOPS = LogicalCharacterAttribute("Upgrades Troops",
                _demonImg.copy(textureId = "5"),
                description = {"Upgrades a unit each turn."})
        val ON_FIRE  =LogicalCharacterAttribute("On Fire",
                _demonImg.copy(textureId = "6"),
                statusEffect = true,
                damageOverTime = LogicalCharacterAttributeTrigger.DamageOverTimeAttribute(
                        1, DamageType.FIRE
                ),
                description = {"This unit is on fire and will take one damage per turn until it's put out."})
    }
}

public data class DamageType(val name: String, val icon: ProtoActor){
    companion object {
        val FIRE = DamageType("fire", DataDrivenOnePageAnimation.EXPLODE)
    }
}

public class FunctionalCharacterAttributeFactory @Inject constructor (val entitySpawner: EntitySpawner,
                                                                      val tacticalMapState: TacticalMapState,
                                                                      val specialEffectManager: SpecialEffectManager,
                                                                      val transientEntityTracker: TransientEntityTracker,
                                                                      val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                                      val damageOperator: DamageOperator){

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
        if (logicalAttribute.explodesOnDeath != null){
            val explosionParams = logicalAttribute.explodesOnDeath
            val funcAttr = ExplodesOnDeathFunctionalAttribute(radius = explosionParams.radius, damage = explosionParams.damage,
                    entitySpawner = entitySpawner, tacticalMapAlgorithms = tacticalMapAlgorithms, damageOperator = damageOperator,
                    tacticalMapState = tacticalMapState)
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
    class DamageOverTimeAttribute(val damage: Int, val damageType: DamageType)
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

    // this is run after all items have been placed on the map, but BEFORE the first turn is taken.
    fun onInitialization(thisCharacter: LogicalCharacter){

    }

    fun onCharacterTurnStart(thisCharacter: LogicalCharacter){

    }
}