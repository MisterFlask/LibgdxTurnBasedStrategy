package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility


sealed class TacticalGuiEvent{
    data class CharacterSelected(val character: LogicalCharacter) : TacticalGuiEvent()
    data class TileClicked(val tileLocation: TileLocation) : TacticalGuiEvent()
    class EndTurnButtonClicked() : TacticalGuiEvent()
    class FinishedEnemyTurn(): TacticalGuiEvent()
    class CharacterUnselected(): TacticalGuiEvent()
    data class StartedHoveringOverAbility(val ability: LogicalAbility) : TacticalGuiEvent()
    data class StoppedHoveringOverAbility(val ability: LogicalAbility) : TacticalGuiEvent()
    data class ClickedButtonToActivateAbility(val ability: LogicalAbility) : TacticalGuiEvent()
    data class SwitchedGuiState(val guiState: BoardInputState):TacticalGuiEvent()
}