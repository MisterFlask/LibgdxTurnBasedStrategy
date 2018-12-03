package com.ironlordbyron.turnbasedstrategy.view

import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter

public class CharacterSpriteUtils{

    fun darkenSprite(character: LogicalCharacter){
        character.actor.color = CharacterTemplates.DIM_COLOR
    }

    fun brightenSprite(character :LogicalCharacter){
        character.actor.color = CharacterTemplates.BRIGHT_COLOR
    }
}