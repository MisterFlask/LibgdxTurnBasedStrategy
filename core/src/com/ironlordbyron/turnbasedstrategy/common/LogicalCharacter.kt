package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.view.animation.LogicalCharacterActorGroup
import java.util.*

/**
 * Represents a mutable character generated from a template.
 * Has a location, an associated actor, and a
 */
data class LogicalCharacter(val actor: LogicalCharacterActorGroup, // NOTE: This is a transient attribute, do not persist
                            var tileLocation: TileLocation,
                            val tacMapUnit: TacMapUnitTemplate,
                            val playerControlled: Boolean,
                            var endedTurn: Boolean = false,
                            var actionsLeft: Int = 2,
                            var maxActionsLeft: Int = 2,
                            var maxHealth: Int = 3,
                            var healthLeft: Int = maxHealth,
                            val equipment: ArrayList<LogicalEquipment> = ArrayList(),
                            val attributes: ArrayList<LogicalCharacterAttribute> = arrayListOf(),
                            val id: UUID = UUID.randomUUID()) {
    init{
        attributes.addAll(tacMapUnit.startingAttributes)
    }

    val abilities: Collection<LogicalAbilityAndEquipment>
        get() = acquireAbilities()

    val playerAlly: Boolean
    get() = playerControlled //TODO: Differentiate if necessary
    val isDead: Boolean
    get() = healthLeft < 1

    private fun acquireAbilities(): Collection<LogicalAbilityAndEquipment> {
        val abilitiesSansEquipment = tacMapUnit.abilities.map{LogicalAbilityAndEquipment(it, null)}
        val abilitiesWithEquipment = ArrayList<LogicalAbilityAndEquipment>()
        for (equip in equipment){
            for (ability in equip.abilityEnabled){
                abilitiesWithEquipment.add(LogicalAbilityAndEquipment(ability, equip))
            }
        }
        return abilitiesSansEquipment + abilitiesWithEquipment
    }
}


data class LogicalAbilityAndEquipment(val ability: LogicalAbility, val equipment: LogicalEquipment?)
