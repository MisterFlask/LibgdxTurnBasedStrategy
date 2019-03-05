package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.OnFireLogicalEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.ShieldsAnotherOrganFunctionalAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
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
                                            // See FunctionalUnitEffect for examples
                                            // The key is an ID corresponding to the effect; the value
                                            // is the parameters to be fed in.
                                            val customEffects: Map<String, Any> = mapOf()){
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
                customEffects = mapOf(OnFireLogicalEffect(1).toPair()),
                description = {"This unit is on fire and will take one damage per turn until it's put out."})
        val STUNNED = LogicalCharacterAttribute("Stunned",
                _demonImg.copy(textureId = "7"),
                statusEffect = true,
                customEffects = mapOf(),
                description = {"This unit is stunned for the round."}
        )
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

@Deprecated("Use custom attributes instead")
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





