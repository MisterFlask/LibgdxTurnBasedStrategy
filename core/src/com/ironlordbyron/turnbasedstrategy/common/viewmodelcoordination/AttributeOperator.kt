package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.FloatingTextGenerator
import javax.inject.Inject
import javax.inject.Singleton

data class ApplyAttributeEvent(
        val logicalCharacter: LogicalCharacter,
        val logicalCharacterAttribute: LogicalCharacterAttribute,
        val stacksToApply: Int = 1): TacticalGameEvent

data class UnapplyAttributeEvent(
        val logicalCharacter: LogicalCharacter,
        val logicalCharacterAttribute: LogicalCharacterAttribute) : TacticalGameEvent

@Singleton
@Autoinjectable
public class AttributeOperator @Inject constructor(val logicHooks: LogicHooks,
                                                   val animationActionQueueProvider: AnimationActionQueueProvider,
                                                   val floatingTextGenerator: FloatingTextGenerator,
                                                   val eventNotifier: EventNotifier,
                                                   val actionManager: ActionManager) : GameEventListener{
    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is ApplyAttributeEvent -> this.applyAttribute(tacticalGameEvent.logicalCharacter, tacticalGameEvent.logicalCharacterAttribute,
                    tacticalGameEvent.stacksToApply)
            is UnapplyAttributeEvent -> this.unapplyAttribute(tacticalGameEvent.logicalCharacter, tacticalGameEvent.logicalCharacterAttribute)
        }
    }

    private fun unapplyAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute) {
        if (logicalCharacterAttribute.tacticalMapProtoActor != null){
            actionManager.despawnAttributeActorAtTileInSequence(logicalCharacterAttribute, logicalCharacter)
        }
        logicalCharacter.tacMapUnit.attributes.removeIf { it.id == logicalCharacterAttribute.id }
    }

    private fun hasAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Boolean {
        return logicalCharacter.attributes.any{it.id == logicalCharacterAttribute.id}
    }

    fun applyAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute,
                       stacksToApply: Int = 1){
        if (hasAttribute(logicalCharacter, logicalCharacterAttribute) and !logicalCharacterAttribute.stackable){
            return
        }

        if (!logicalCharacter.actor.attributeActors.containsKey(logicalCharacterAttribute.id)
                && logicalCharacterAttribute.tacticalMapProtoActor != null){
            actionManager.spawnAttributeActorAtTileInSequence(
                    logicalCharacterAttribute, logicalCharacter)
        }

        animationActionQueueProvider.addAction(
                floatingTextGenerator.getTemporaryAnimationActorActionPair("${logicalCharacterAttribute.name}", logicalCharacter.tileLocation))

        if (hasAttribute(logicalCharacter, logicalCharacterAttribute) and logicalCharacterAttribute.stackable){
            logicalCharacterAttribute.stacks += 1
        }
        if (!hasAttribute(logicalCharacter,logicalCharacterAttribute)){
            logicalCharacter.tacMapUnit.attributes.add(logicalCharacterAttribute)
        }
        logicHooks.afterApplicationOfAttribute(logicalCharacter, logicalCharacterAttribute,
                stacksToApply)
    }
}