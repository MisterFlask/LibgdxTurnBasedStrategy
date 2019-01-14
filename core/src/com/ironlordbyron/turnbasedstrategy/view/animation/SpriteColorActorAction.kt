package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter

object SpriteColorActorAction{
    fun build(logicalCharacter: LogicalCharacter, color : Color): ActorActionPair{
        return ActorActionPair(logicalCharacter.actor.characterActor, Actions.color(color))
    }

    val DIM_COLOR = Color(.5f,.5f,.5f, 1f)
    val BRIGHT_COLOR = Color.WHITE
}