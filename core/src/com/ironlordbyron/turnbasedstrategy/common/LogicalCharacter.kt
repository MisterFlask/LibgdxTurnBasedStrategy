package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.ironlordbyron.turnbasedstrategy.ai.Intent
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.RangeStyle
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
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
                            val id: UUID = UUID.randomUUID(),
                            var intent: Intent = Intent.None()) {

    val abilities: Collection<LogicalAbilityAndEquipment>
        get() = acquireAbilities()
    fun abilitiesForIntent(intent: IntentType): List<LogicalAbilityAndEquipment> {
        return acquireAbilities().filter{it.ability.intentType == intent}
    }
    val healthLeft: Int
        get() = tacMapUnit.healthLeft
    val maxActionsLeft: Int
        get() = tacMapUnit.maxActionsLeft
    val equipment: Collection<LogicalEquipment>
        get() = tacMapUnit.equipment
    val maxHealth: Int
        get() = tacMapUnit.maxHealth

    val playerAlly: Boolean
    get() = playerControlled //TODO: Differentiate if necessary
    val isDead: Boolean
    get() = tacMapUnit.healthLeft < 1

    public data class StacksOfAttribute(val stacks: Int, val logicalAttribute: LogicalCharacterAttribute)

    private fun acquireAbilities(): Collection<LogicalAbilityAndEquipment> {
        val abilitiesSansEquipment = tacMapUnit.abilities.map{LogicalAbilityAndEquipment(it, null)}
        val abilitiesWithEquipment = ArrayList<LogicalAbilityAndEquipment>()
        for (equip in tacMapUnit.equipment){
            for (ability in equip.abilityEnabled){
                abilitiesWithEquipment.add(LogicalAbilityAndEquipment(ability, equip))
            }
        }
        return abilitiesSansEquipment + abilitiesWithEquipment
    }

    fun getAttributes() : Collection<LogicalCharacter.StacksOfAttribute> {
        return tacMapUnit.getAttributes()
    }
    fun getStacks(logicalAttribute: LogicalCharacterAttribute) : StacksOfAttribute {
        return tacMapUnit.getAttributes().find{it.logicalAttribute.id == logicalAttribute.id}!!
    }

    fun incrementAttribute(logicalAttribute: LogicalCharacterAttribute, stacks: Int){
        tacMapUnit.incrementAttribute(logicalAttribute, stacks)
    }
}


data class LogicalAbilityAndEquipment(val ability: LogicalAbility, val equipment: LogicalEquipment?){
    companion object {
        val mapAlgorithms:TacticalMapAlgorithms by lazy {
            GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
        }
    }

    override fun toString() : String{
        return ability.name
    }

    fun getSquaresInRangeOfAbility(sourceSquare: TileLocation, logicalCharacter: LogicalCharacter): Collection<TileLocation> {
        return ability.rangeStyle.getTargetableTiles(logicalCharacter, this, sourceSquare)
    }
}
