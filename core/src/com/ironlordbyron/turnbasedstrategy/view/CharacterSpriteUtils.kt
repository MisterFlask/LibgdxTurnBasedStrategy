package com.ironlordbyron.turnbasedstrategy.view

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.view.animation.SpriteColorActorAction

public class CharacterSpriteUtils{

    fun darkenSprite(character: LogicalCharacter){
        character.actor.color = SpriteColorActorAction.DIM_COLOR
    }

    fun brightenSprite(character :LogicalCharacter){
        character.actor.color = SpriteColorActorAction.BRIGHT_COLOR
    }
}