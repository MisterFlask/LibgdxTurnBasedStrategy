package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.kotcrab.vis.ui.building.utilities.Alignment
import java.time.Duration

public class PulseAnimationGenerator {
    val scaleFactor = .4f

    fun generateActorActionPair(actor: Actor, durationInSeconds: Float): ActorActionPair {
        val actions = generateAction(durationInSeconds)
        actor.setOrigin(Alignment.CENTER.alignment)
        return ActorActionPair(actor, actions)
    }
    private fun generateAction(durationInSeconds: Float): Action {
        val actions =
                Actions.sequence(
                        Actions.scaleBy(scaleFactor, scaleFactor, durationInSeconds/2),
                        Actions.scaleBy(-1 * scaleFactor, -1 * scaleFactor, durationInSeconds/2)
                )
        return actions


    }

    public fun foreverAction() : Action{
        return Actions.forever(generateAction(.5f))
    }


}