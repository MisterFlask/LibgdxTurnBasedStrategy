package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import javax.inject.Inject
import javax.inject.Singleton

interface BoardInputState{
    abstract val name: String
    data class UnitSelected(val unit: LogicalCharacter, override val name: String = "UnitSelected") : BoardInputState
    data class DefaultState(override val name: String = "DefaultState") : BoardInputState
    data class PlayerIntendsToUseAbility(val unit: LogicalCharacter, val ability: LogicalAbility, override val name: String = "PlayerIntendsToUseAbility"):BoardInputState
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

    var boardInputState : BoardInputState = BoardInputState.DefaultState()
        set(value) {
            println("Setting board input state: $value")
            eventNotifier.notifyListeners(TacticalGuiEvent.SwitchedGuiState(value))
            field = value
        }

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
                    abilityController.signalIntentToActOnAbility(selectedCharacter, event.ability)
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
            if (abilityController.canUseAbilityOnSquare(currentBoardInputState.unit, currentBoardInputState.ability, character, location)){
                abilityController.useAbility(currentBoardInputState.unit, currentBoardInputState.ability, character, location)
                boardInputState = BoardInputState.DefaultState()
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
            boardInputState = BoardInputState.DefaultState()
            selectedCharacter = null
            eventNotifier.notifyListeners(TacticalGuiEvent.CharacterUnselected())
        }
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