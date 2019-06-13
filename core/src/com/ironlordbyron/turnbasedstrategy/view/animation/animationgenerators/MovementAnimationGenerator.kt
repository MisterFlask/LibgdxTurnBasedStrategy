package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimationSpeedManager
import javax.inject.Inject

class MovementAnimationGenerator @Inject constructor(val logicalTileTracker: LogicalTileTracker
                                                     ){

    fun createMoveActorToTileAction(tile: TileLocation) : Action{
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(tile)
        var moveAction : Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f / AnimationSpeedManager.animationSpeedScale)
        return moveAction
    }

    fun createMoveActorToTileActorActionPair(tile: TileLocation, actor: Actor, actorOnlyExistsDuringAnimation: Boolean = false) : ActorActionPair{
        if (actorOnlyExistsDuringAnimation){
            actor.isVisible = false
        }
        val actionPair = ActorActionPair(actor, createMoveActorToTileAction(tile),
                murderActorsOnceCompletedAnimation = actorOnlyExistsDuringAnimation,
                actionOnceAnimationCompletes = {
                    if (actorOnlyExistsDuringAnimation) {
                        actor.remove()
                    }
                })
        return actionPair
    }
}