package com.ironlordbyron.turnbasedstrategy.controller

import com.badlogic.gdx.graphics.Color
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import javax.inject.Inject
import javax.inject.Singleton

sealed class BoardInputState{
    data class UnitSelected(val unit: LogicalCharacter) : BoardInputState()
    object DefaultState : BoardInputState()
    data class PlayerIntendsToUseAbility(val unit: LogicalCharacter, val ability: LogicalAbility): BoardInputState()
}

/**
 *  Responsible for handling player inputs.
 */
@Singleton
class TacticalMapController @Inject constructor(val gameBoardOperator: GameBoardOperator,
                                                val eventNotifier: EventNotifier,
                                                val boardState: TacticalMapState,
                                                val abilityController: AbilityController,
                                                val mapHighlighter: MapHighlighter,
                                                val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                val abilityEffectFactory: AbilityEffectFactory) : EventListener {

    var boardInputState : BoardInputState = BoardInputState.DefaultState
    var selectedCharacter: LogicalCharacter? = null
    var playerHasPriority = true
    override fun consumeEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.TileClicked -> {
                if (playerHasPriority) playerClickedOnTile(event.tileLocation)
            }
            is TacticalGuiEvent.EndTurnButtonClicked -> {
                if (playerHasPriority) {
                    playerHasPriority = false
                    gameBoardOperator.endTurn()
                }
            }
            is TacticalGuiEvent.FinishedEnemyTurn -> {
                playerHasPriority = true
            }
            is TacticalGuiEvent.ClickedButtonToActivateAbility -> {
                val selectedCharacter = selectedCharacter
                if (selectedCharacter == null){
                    throw IllegalStateException("Clicked button to activate ability when no char selected")
                }
                boardInputState = BoardInputState.PlayerIntendsToUseAbility(selectedCharacter,
                        event.ability)
                if (event.ability.abilityClass == AbilityClass.TARGETED_ABILITY){
                    abilityController.SignalIntentToActOnAbility(selectedCharacter, event.ability)
                }
            }
        }
    }

    init{
        eventNotifier.registerListener(this)
    }

    fun playerClickedOnTile(location: TileLocation){
        val character = tacticalMapAlgorithms.getCharacterAtLocation(location)
        mapHighlighter.killHighlights()
        val currentBoardInputState = boardInputState
        if (currentBoardInputState is BoardInputState.PlayerIntendsToUseAbility){
            if (isValidTargetForAbility(currentBoardInputState.ability, location)){
                performAbilityOnLocation(currentBoardInputState.ability, location)
            }

            return
        }

        if (character != null){
            selectCharacterInTacMap(character)
            selectedCharacter = character
            eventNotifier.notifyListeners(TacticalGuiEvent.CharacterSelected(character))
        }else{
            val currentBoardInputState = boardInputState
            when(currentBoardInputState){
                is BoardInputState.UnitSelected ->  moveUnitIfAble(currentBoardInputState.unit, location)
            }
            boardInputState = BoardInputState.DefaultState
            selectedCharacter = null
            eventNotifier.notifyListeners(TacticalGuiEvent.CharacterUnselected())
        }
    }

    // TODO
    private fun performAbilityOnLocation(ability: LogicalAbility, location: TileLocation) {
        return
    }

    // TODO
    private fun isValidTargetForAbility(ability: LogicalAbility, location: TileLocation): Boolean {
        return true
    }

    private fun selectCharacterInTacMap(character: LogicalCharacter) {
        boardInputState = BoardInputState.UnitSelected(character)
        if (character.playerControlled){
            val tilesToHighlight = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(character)
            mapHighlighter.highlightTiles(tilesToHighlight, HighlightType.GREEN_TILE)
        }
    }

    fun canUnitMoveTo(location: TileLocation, unit: LogicalCharacter): Boolean {
        if (!unit.playerControlled){
            return false // TODO: Inelegant
        }
        return tacticalMapAlgorithms.getWhereCharacterCanMoveTo(unit).contains(location)
    }

    private fun moveUnitIfAble(unit: LogicalCharacter, location: TileLocation) {
        if (canUnitMoveTo(location, unit)){
            gameBoardOperator.moveCharacterToTile(unit, location, false, wasPlayerInitiated = true);
        }
    }
}