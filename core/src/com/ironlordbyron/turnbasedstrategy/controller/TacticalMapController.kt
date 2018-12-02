package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.*
import javax.inject.Inject
import javax.inject.Singleton

sealed class BoardInputState{
    data class UnitSelected(val unit: LogicalCharacter) : BoardInputState()
    object DefaultState : BoardInputState()
}

/**
 *  Responsible for handling player inputs.
 */
@Singleton
class TacticalMapController @Inject constructor(val gameBoardOperator: GameBoardOperator,
                                                val eventNotifier: EventNotifier,
                                                val boardState: TacticalMapState) : EventListener {

    var boardInputState : BoardInputState = BoardInputState.DefaultState
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
        }
    }

    init{
        eventNotifier.registerListener(this)
    }



    fun playerClickedOnTile(location: TileLocation){
        val character = boardState.getCharacterAtLocation(location)
        gameBoardOperator.killHighlights()
        if (character != null){
            selectCharacterInTacMap(character)
            eventNotifier.notifyListeners(TacticalGuiEvent.CharacterSelected(character))
        }else{
            val currentBoardInputState = boardInputState
            when(currentBoardInputState){
                is BoardInputState.UnitSelected ->  moveUnitIfAble(currentBoardInputState.unit, location)
            }
            boardInputState = BoardInputState.DefaultState
        }
    }

    private fun selectCharacterInTacMap(character: LogicalCharacter) {
        boardInputState = BoardInputState.UnitSelected(character)
        if (character.playerControlled){
            val tilesToHighlight = boardState.getWhereCharacterCanMoveTo(character)
            gameBoardOperator.highlightTiles(tilesToHighlight, HighlightType.GREEN_TILE)
        }
    }


    fun canUnitMoveTo(location: TileLocation, unit: LogicalCharacter): Boolean {
        if (!unit.playerControlled){
            return false // TODO: Inelegant
        }
        return boardState.getWhereCharacterCanMoveTo(unit).contains(location)
    }

    private fun moveUnitIfAble(unit: LogicalCharacter, location: TileLocation) {
        if (canUnitMoveTo(location, unit)){
            gameBoardOperator.moveCharacterToTile(unit, location);
        }
    }
}