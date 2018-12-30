package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import javax.inject.Inject

class MovementAnimationGenerator @Inject constructor(val logicalTileTracker: LogicalTileTracker
                                                     ){

    fun createMoveActorToTileAction(tile: TileLocation) : Action{
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(tile)
        var moveAction : Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f)
        return moveAction
    }

    fun createMoveActorToTileActorActionPair(tile: TileLocation, actor: Actor, removeActorOnceAnimationIsComplete: Boolean = false) : ActorActionPair{
        val actionPair = ActorActionPair(actor, createMoveActorToTileAction(tile), murderActorsOnceCompletedAnimation = removeActorOnceAnimationIsComplete)
        return actionPair
    }
}