package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.TransientEntityTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ImageIcon
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import java.util.*
import javax.inject.Inject

/**
 * So, here's how this works: Each attribute corresponds to a visible icon for the player to interact with.
 * it may have ANY of the provided attributes, and new attributes should be added by .
 */
public data class LogicalCharacterAttribute(val name: String,
                                            val imageIcon: ProtoActor,
                                            val shieldsAnotherOrgan: LogicalCharacterAttributeTrigger.ShieldsAnotherOrgan? = null,
                                            val masterOrgan: Boolean = false,
                                            val organ: Boolean = false,
                                            val description: (LogicalCharacterAttribute) -> String,
                                            val statusEffect: Boolean = false,
                                            // See FunctionalUnitEffect for examples
                                            // The key is an ID corresponding to the effect; the value
                                            // is the parameters to be fed in.
                                            val customEffects: Map<String, Any> = mapOf(),
                                            val stackable: Boolean = false,
                                            var stacks: Int = 1,
                                            val id: String = name,
                                            val tacticalMapProtoActor: ProtoActor? = null,
                                            val tacticalMapProtoActorOffsetX: Int = 0,
                                            val tacticalMapProtoActorOffsetY: Int = 0){
    companion object {
        val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        val _painterlyIcon = ImageIcon(ImageIcon.PAINTERLY_FOLDER, "fire-arrows-1.png")
        val EXPLODES_ON_DEATH = LogicalCharacterAttribute("Explodes On Death",
                _painterlyIcon,
                customEffects = mapOf(ExplodesOnDeath(4, 5).toEntry()),
                description = {"Explodes on death, dealing 5 damage to everything in a 4-tile radius"})
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
        val STUNNED = LogicalCharacterAttribute("Stunned",
                _demonImg.copy(textureId = "7"),
                statusEffect = true,
                customEffects = mapOf(),
                description = {"This unit is stunned for the round."})
        val ON_FIRE = LogicalCharacterAttribute("On Fire",
                _demonImg.copy(textureId = "8"),
                statusEffect = true,
                customEffects = hashMapOf(OnFireLogicalEffect(1).toPair()),
                description = {"This character is on fire."},
                tacticalMapProtoActor = DataDrivenOnePageAnimation.FIRE
        )
        val SLIMED: LogicalCharacterAttribute = LogicalCharacterAttribute("Slimed",
                _demonImg.copy(textureId = "9"),
                statusEffect = true,
                customEffects = hashMapOf(SlimedUnitLogicalEffect().toEntry()),
                description = {"This character is slimed."},
                stackable = true,
                stacks = 1
        )
        val SNOOZING: LogicalCharacterAttribute = LogicalCharacterAttribute("Unaware",
                _demonImg.copy(textureId="10"),
                statusEffect = true,
                customEffects = hashMapOf(SnoozeLogicalUnitEffect().toEntry()),
                description = {"This character is unaware."},
                stackable = false,
                stacks = 1,
                tacticalMapProtoActor = DataDrivenOnePageAnimation.SNOOZE_ACTOR,
                tacticalMapProtoActorOffsetY = 6
                )



    }
}

public data class DamageType(val name: String, val icon: ProtoActor){
    companion object {
        val FIRE = DamageType("fire", DataDrivenOnePageAnimation.EXPLODE)
    }
}

@Deprecated("Use logicHooks instead")
public class FunctionalCharacterAttributeFactory @Inject constructor (val actionManager: ActionManager,
                                                                      val tacticalMapState: TacticalMapState,
                                                                      val specialEffectManager: SpecialEffectManager,
                                                                      val transientEntityTracker: TransientEntityTracker,
                                                                      val damageOperator: DamageOperator){

    fun getFunctionalAttributesForCharacter(logicalCharacter: LogicalCharacter): List<FunctionalCharacterAttribute> {
        return logicalCharacter.attributes.flatMap{getFunctionalAttributesFromLogicalAttribute(it, logicalCharacter)}
    }

    fun getFunctionalAttributesFromLogicalAttribute(logicalAttribute: LogicalCharacterAttribute, character: LogicalCharacter) : Collection<FunctionalCharacterAttribute>{
        val attrsList = ArrayList<FunctionalCharacterAttribute>()
        if (logicalAttribute.shieldsAnotherOrgan != null){
            val funcAttr = ShieldsAnotherOrganFunctionalAttribute(actionManager, tacticalMapState, specialEffectManager, logicalAttribute,
                    transientEntityTracker)
            attrsList.add(funcAttr)
        }
        return attrsList
    }


}

@Deprecated("Use custom attributes instead")
public interface LogicalCharacterAttributeTrigger{
    // The below are Organ abilities

    data class ShieldsAnotherOrgan(var characterShieldedId: UUID? = null,
                                   var _characterShieldActorId: UUID? = null,// transient attribute
                                   var _lineActorId: UUID? = null // transient attribute
    ): LogicalCharacterAttributeTrigger
    class MasterOrgan: LogicalCharacterAttributeTrigger
    class Organ: LogicalCharacterAttributeTrigger
}





