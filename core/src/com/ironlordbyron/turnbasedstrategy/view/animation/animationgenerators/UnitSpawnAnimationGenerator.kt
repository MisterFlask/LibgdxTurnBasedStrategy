package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.ActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import javax.inject.Inject

public class UnitSpawnAnimationGenerator @Inject constructor(val animationQueueProvider: ActionQueueProvider,
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