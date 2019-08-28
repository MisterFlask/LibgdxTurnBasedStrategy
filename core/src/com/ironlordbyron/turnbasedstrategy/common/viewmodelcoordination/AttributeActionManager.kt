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
public class AttributeActionManager @Inject constructor(val logicHooks: LogicHooks,
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

    public fun unapplyAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute,
                                textPopup: String? = null) {
        if (logicalCharacterAttribute.tacticalMapProtoActor != null){
            actionManager.despawnAttributeActorAtTileInSequence(logicalCharacterAttribute, logicalCharacter)
        }
        if (textPopup != null){
            actionManager.risingText("!!!", logicalCharacter.tileLocation)
        }
        logicalCharacter.tacMapUnit.removeAttribute(logicalCharacterAttribute)
    }

    private fun hasAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Boolean {
        return logicalCharacter.getAttributes().any{it.logicalAttribute.id == logicalCharacterAttribute.id}
    }


    fun applyAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute,
                       stacksToApply: Int = 1, popup: String? = null){
        if (hasAttribute(logicalCharacter, logicalCharacterAttribute) and !logicalCharacterAttribute.stackable){
            return
        }
        if (stacksToApply == 0){
            return
        }

        if (!logicalCharacter.actor.attributeActors.containsKey(logicalCharacterAttribute.id)
                && logicalCharacterAttribute.tacticalMapProtoActor != null){
            actionManager.spawnAttributeActorAtTileInSequence(
                    logicalCharacterAttribute, logicalCharacter)
        }

        if (popup!= null){
            actionManager.risingText(popup, logicalCharacter.tileLocation)
        }

        animationActionQueueProvider.addAction(
                floatingTextGenerator.getTemporaryAnimationActorActionPair("${logicalCharacterAttribute.name}", logicalCharacter.tileLocation))


        if (hasAttribute(logicalCharacter, logicalCharacterAttribute) and logicalCharacterAttribute.stackable){
            logicalCharacter.incrementAttribute(logicalCharacterAttribute, stacksToApply)
            logicHooks.afterApplicationOfAttribute(logicalCharacter, logicalCharacterAttribute,
                    stacksToApply)
        }
        else if (!hasAttribute(logicalCharacter,logicalCharacterAttribute)){
            logicalCharacter.incrementAttribute(logicalCharacterAttribute, stacksToApply)
            logicHooks.afterApplicationOfAttribute(logicalCharacter, logicalCharacterAttribute,
                    stacksToApply)
        } else{
            return
        }
    }
}