package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair

public class TextUpdateAnimationGenerator{
    fun generateActorActionPair(textLabel: Label, textToSwitchTo: String, parentObject : Actor) : ActorActionPair{
        return ActorActionPair(parentObject, TextAction(textLabel, textToSwitchTo))
    }
}

private class TextAction(val textLabel : Label, val toText: String) : Action(){
    override fun act(delta: Float): Boolean {
        textLabel.setText(toText)
        return true
    }
}