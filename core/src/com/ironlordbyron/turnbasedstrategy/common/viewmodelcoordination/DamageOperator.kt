package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.LogicalAbilityAndEquipment
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.controller.GameEventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.CharacterModificationAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.FloatingTextGenerator
import javax.inject.Inject

class DamageOperator @Inject constructor(val characterModificationAnimationGenerator: CharacterModificationAnimationGenerator,
                                         val animationActionQueueProvider: AnimationActionQueueProvider,
                                         val visibleCharacterDataFactory: VisibleCharacterDataFactory,
                                         val floatingTextGenerator: FloatingTextGenerator,
                                         val gameEventNotifier: GameEventNotifier,
                                         val eventNotifier: GameEventNotifier){

    fun damageCharacter(targetCharacter: LogicalCharacter,
                        damageAmount: Int,
                        abilityAndEquipment: LogicalAbilityAndEquipment?,
                        sourceCharacter: LogicalCharacter?) {
        if (targetCharacter.isDead){
            return // doesn't matter if we're just kicking a dead horse
        }
        targetCharacter.tacMapUnit.healthLeft -= damageAmount // TODO: Not the responsibility of this class
        val secondaryActions = arrayListOf(
                characterModificationAnimationGenerator.getCharacterShudderActorActionPair(logicalCharacter = targetCharacter),
                characterModificationAnimationGenerator.getCharacterTemporaryDarkenActorActionPair(logicalCharacter = targetCharacter)

        )
        animationActionQueueProvider.addAction(
                floatingTextGenerator.getTemporaryAnimationActorActionPair("${damageAmount}", targetCharacter.tileLocation)
                        .copy(secondaryActions = secondaryActions
                        ))
        visibleCharacterDataFactory.updateCharacterHpMarkerInSequence(targetCharacter)
        if (targetCharacter.isDead){
            gameEventNotifier.notifyListenersOfGameEvent(TacticalGameEvent.UnitKilled(targetCharacter))
        }else{
            if (abilityAndEquipment != null){
                for (effect in abilityAndEquipment.ability.inflictsStatusAffect){
                    eventNotifier.notifyListenersOfGameEvent(ApplyAttributeEvent(targetCharacter, effect, 1))
                }
            }
        }
        Logging.DebugCombatLogic("Attack from " + sourceCharacter?.tileLocation + " to " + targetCharacter.tileLocation + "; attack range = " + abilityAndEquipment?.ability?.range)
        val struckEvent = UnitWasStruckEvent(targetCharacter, damageAmount, abilityAndEquipment)
        eventNotifier.notifyListenersOfGameEvent(struckEvent)
    }
}

data class UnitWasStruckEvent(val targetCharacter: LogicalCharacter, val damageAmount: Int, val abilityAndEquipment: LogicalAbilityAndEquipment?) : TacticalGameEvent
