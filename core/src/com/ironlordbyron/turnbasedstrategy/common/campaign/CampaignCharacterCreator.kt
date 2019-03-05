package com.ironlordbyron.turnbasedstrategy.common.campaign

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate


public class CampaignCharacterCreator{

    fun generateTacMapUnitTemplate(characterClass: CharacterClass): TacMapUnitTemplate {
        return TacMapUnitTemplate(movesPerTurn = characterClass.movesPerTurn,
                tiledTexturePath = characterClass.protoActor,
                templateName = characterClass.name,
                attributes = ArrayList(characterClass.startingAttributes),
                allowedEquipment = ArrayList(characterClass.allowedEquipment),
                maxActionsLeft = 2,
                abilities = ArrayList(characterClass.startingAbilities))
    }
}

