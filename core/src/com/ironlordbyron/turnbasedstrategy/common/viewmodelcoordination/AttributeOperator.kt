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

@Singleton
@Autoinjectable
public class AttributeOperator @Inject constructor(val logicHooks: LogicHooks,
                                                   val animationActionQueueProvider: AnimationActionQueueProvider,
                                                   val floatingTextGenerator: FloatingTextGenerator,
                                                   val eventNotifier: EventNotifier) : GameEventListener{
    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is ApplyAttributeEvent -> this.applyAttribute(tacticalGameEvent.logicalCharacter, tacticalGameEvent.logicalCharacterAttribute,
                    tacticalGameEvent.stacksToApply)
        }
    }

    private fun hasAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Boolean {
        return logicalCharacter.attributes.any{it.id == logicalCharacterAttribute.id}
    }

    fun applyAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute,
                       stacksToApply: Int = 1){
        if (hasAttribute(logicalCharacter, logicalCharacterAttribute) and !logicalCharacterAttribute.stackable){
            return
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
        animationActionQueueProvider.runThroughActionQueue()
    }
}