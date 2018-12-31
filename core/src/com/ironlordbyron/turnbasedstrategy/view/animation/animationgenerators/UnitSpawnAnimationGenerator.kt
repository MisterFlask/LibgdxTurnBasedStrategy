package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import javax.inject.Inject

// TODO: Merge wtih revealActionGenerator, since they serve similar functions
public class UnitSpawnAnimationGenerator @Inject constructor(val animationQueueProviderAnimation: AnimationActionQueueProvider,
                                                             val temporaryAnimationGenerator: TemporaryAnimationGenerator){

    fun createUnitSpawnAnimation(logicalCharacter: LogicalCharacter) : ActorActionPair {
        logicalCharacter.actor.isVisible = false
        var action = temporaryAnimationGenerator.getTemporaryAnimationActorActionPair(
                logicalCharacter.tileLocation, DataDrivenOnePageAnimation.EXPLODE
        )
        action = action.copy(actionOnceAnimationCompletes = {
            logicalCharacter.actor.isVisible = true
        })
        return action
    }
}