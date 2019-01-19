package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair

// make character shudder a bit when taking damage
public class CharacterModificationAnimationGenerator{
    fun getCharacterShudderActorActionPair(logicalCharacter: LogicalCharacter): ActorActionPair {
        val action = Actions.sequence(
                Actions.moveBy(2.0f, 0.0f, 0.1f, Interpolation.bounceOut),
                Actions.moveBy(-2.0f, 0.0f, 0.1f, Interpolation.bounceOut),
                Actions.moveBy(-2.0f, 0.0f, 0.1f, Interpolation.bounceOut),
                Actions.moveBy(2.0f, 0.0f, 0.1f, Interpolation.bounceOut)
                )
        return ActorActionPair(logicalCharacter.actor.characterActor, action)
    }

    fun getCharacterTemporaryDarkenActorActionPair(logicalCharacter: LogicalCharacter): ActorActionPair {
        val originalColor = logicalCharacter.actor.characterActor.color
        val action = Actions.sequence(Actions.color(Color.RED, .2f, Interpolation.linear), Actions.color(originalColor, .2f, Interpolation.linear))
        return ActorActionPair(logicalCharacter.actor.characterActor, action)
    }


}