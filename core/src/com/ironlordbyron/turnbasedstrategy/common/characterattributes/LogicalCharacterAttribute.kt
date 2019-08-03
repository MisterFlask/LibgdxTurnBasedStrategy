package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.*
import com.ironlordbyron.turnbasedstrategy.tacmapunits.ExplodesOnDeathFunctionalUnitEffect
import com.ironlordbyron.turnbasedstrategy.tacmapunits.ShieldsAnotherOrganFunctionalAttribute
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ImageIcon
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

/**
 * So, here's how this works: Each attribute corresponds to a visible icon for the player to interact with.
 * it may have ANY of the provided attributes, and new attributes should be added by .
 */
public open class LogicalCharacterAttribute(val name: String,
                                            val imageIcon: ProtoActor,
                                            val masterOrgan: Boolean = false,
                                            val organ: Boolean = false,
                                            val description: (Int) -> String,
                                            val statusEffect: Boolean = false,
                                            // See FunctionalAttributeEffect for examples
                                            // The key is an ID corresponding to the effect; the value
                                            // is the parameters to be fed in.
                                            val customEffects: Collection<FunctionalAttributeEffect> = listOf(),
                                            val stackable: Boolean = false,
                                            val id: String = name,
                                            val tacticalMapProtoActor: ProtoActor? = null,
                                            val tacticalMapProtoActorOffsetX: Int = 0,
                                            val tacticalMapProtoActorOffsetY: Int = 0){
    companion object {
        val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        val _painterlyIcon = ImageIcon(ImageIcon._PAINTERLY_FOLDER, "fire-arrows-1.png")
        val EXPLODES_ON_DEATH = LogicalCharacterAttribute("Explodes On Death",
                _painterlyIcon,
                customEffects = listOf(ExplodesOnDeathFunctionalUnitEffect(4, 5)),
                description = {"Explodes on death, dealing 5 damage to everything in a 4-tile radius"})
        val MASTER_ORGAN = LogicalCharacterAttribute("Master Organ",
                _demonImg.copy(textureId = "3"),
                masterOrgan= true,
                description = {"Master organ.  When destroyed, the fortress will begin sinking back into Hell."})
        val SHIELDS_ANOTHER_ORGAN = LogicalCharacterAttribute("Shields Organ",
                _demonImg.copy(textureId = "4"),
                customEffects = listOf(ShieldsAnotherOrganFunctionalAttribute()),
                description = {"Shields an organ from all damage."})
        val UPGRADES_TROOPS = LogicalCharacterAttribute("Upgrades Troops",
                _demonImg.copy(textureId = "5"),
                description = {"Upgrades a unit each turn."})
        val STUNNED = LogicalCharacterAttribute("Stunned",
                _demonImg.copy(textureId = "7"),
                statusEffect = true,
                customEffects = listOf(), //todo
                description = {"This unit is stunned for the round."})
        val ON_FIRE = LogicalCharacterAttribute("On Fire",
                _demonImg.copy(textureId = "8"),
                statusEffect = true,
                customEffects = listOf(OnFireFunctionalEffect(1)),
                description = {stacks -> "This character is on fire and takes ${stacks} damage per turn."},
                tacticalMapProtoActor = DataDrivenOnePageAnimation.FIRE
        )
        val SLIMED: LogicalCharacterAttribute = LogicalCharacterAttribute("Slimed",
                _demonImg.copy(textureId = "9"),
                statusEffect = true,
                customEffects = listOf(SlimedUnitFunctionalEffect()),
                description = {stacks -> "This character's movement rate is reduced by ${stacks}}."},
                stackable = true
        )


    }
}

public data class DamageType(val name: String, val icon: ProtoActor){
    companion object {
        val FIRE = DamageType("fire", DataDrivenOnePageAnimation.EXPLODE)
    }
}




