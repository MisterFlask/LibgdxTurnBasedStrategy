package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.LogicalAbilityAndEquipment
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.ScenarioParams
import java.util.*


sealed class TacticalGuiEvent{
    data class CycleUnitCarousel(val characterIdSelected : UUID? = null) : TacticalGuiEvent()
    data class CharacterSelected(val character: LogicalCharacter) : TacticalGuiEvent()
    data class TileClicked(val tileLocation: TileLocation) : TacticalGuiEvent()
    class EndTurnButtonClicked() : TacticalGuiEvent()
    class FinishedEnemyTurn(): TacticalGuiEvent()
    class CharacterUnselected(): TacticalGuiEvent()
    data class StartedHoveringOverAbility(val abilityEquipmentPair: LogicalAbilityAndEquipment) : TacticalGuiEvent()
    data class StoppedHoveringOverAbility(val abilityEquipmentPair: LogicalAbilityAndEquipment) : TacticalGuiEvent()
    data class ClickedButtonToActivateAbility(val abilityEquipmentPair: LogicalAbilityAndEquipment) : TacticalGuiEvent()
    data class SwitchedGuiState(val guiState: BoardInputState):TacticalGuiEvent()
    data class TileHovered(val location: TileLocation) : TacticalGuiEvent()

    // screen swap events
    class SwapToTacticsScreen: TacticalGuiEvent()
    class SwapToMainMenu: TacticalGuiEvent()
    data class ScenarioStart(val scenarioParams: ScenarioParams) : TacticalGuiEvent()
    data class PlayerIsPlacingUnit(val unit: TacMapUnitTemplate) : TacticalGuiEvent()
}
