package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation


sealed class TacticalGuiEvent{
    data class CharacterSelected(val character: LogicalCharacter) : TacticalGuiEvent()
    data class TileClicked(val tileLocation: TileLocation) : TacticalGuiEvent()
    class EndTurnButtonClicked() : TacticalGuiEvent()
    class FinishedEnemyTurn(): TacticalGuiEvent()
}