package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.ai.Intent
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.ai.goals.Goal
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tacmapunits.NullAiMetaGoal
import com.ironlordbyron.turnbasedstrategy.view.animation.LogicalCharacterActorGroup
import com.ironlordbyron.turnbasedstrategy.view.animation.animationlisteners.DeathGameEventHandler
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
                            var intent: Intent = Intent.None(),
                            var goal: Goal? = null) {

    val id: UUID
        get() = tacMapUnit.unitId

    fun hasAttribute(logicalAttribute: LogicalCharacterAttribute): Boolean {
        return this.getAttributes().any{it.logicalAttribute.id == logicalAttribute.id && it.stacks > 0}
    }

    fun hasAttributeWithId(attributeId: String): Boolean {
        return this.getAttributes().any{it.logicalAttribute.id == attributeId && it.stacks > 0}
    }

    override fun toString(): String {
        return "$tileLocation : ${tacMapUnit.templateName}"
    }

    val abilities: Collection<LogicalAbilityAndEquipment>
        get() = tacMapUnit.abilities

    fun abilitiesForIntent(intent: IntentType): List<LogicalAbilityAndEquipment> {
        return tacMapUnit.abilities.filter { it.ability.intentType == intent }
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

    fun getAttributes(): Collection<StacksOfAttribute> {
        return tacMapUnit.getAttributes()
    }

    fun getStacks(logicalAttribute: LogicalCharacterAttribute): StacksOfAttribute {
        return tacMapUnit.getAttributes().find { it.logicalAttribute.id == logicalAttribute.id }!!
    }

    fun incrementAttribute(logicalAttribute: LogicalCharacterAttribute, stacks: Int) {
        tacMapUnit.incrementAttribute(logicalAttribute, stacks)
    }

    val actionManager: ActionManager by LazyInject(ActionManager::class.java)
    val deathGameEventHandler: DeathGameEventHandler by LazyInject(DeathGameEventHandler::class.java)

    fun killAndDespawn() {
        this.tacMapUnit.healthLeft = 0
        deathGameEventHandler.handleUnitKilledEvent(this)
        actionManager.despawnEntityInSequence(this.actor)
    }

    fun formulateNewIntent(){
        if (this.tacMapUnit.metagoal is NullAiMetaGoal || this.tacMapUnit.enemyAiType == EnemyAiType.IMMOBILE_UNIT){
            return
        }
        if (this.goal == null){
            this.goal = this.tacMapUnit.metagoal.formulateNewGoal(this)
        }
        this.intent = this.goal!!.formulateIntent(this)
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
                .filter{this.ability.abilityUsageTileFilter.tileIsValid(it)}
    }
}
