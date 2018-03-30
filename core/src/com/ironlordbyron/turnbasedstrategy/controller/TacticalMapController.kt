package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.*
import javax.inject.Inject
import javax.inject.Singleton

sealed class BoardInputState{
    data class UnitSelected(val unit: LogicalCharacter) : BoardInputState()
    object DefaultState : BoardInputState()
}

/**
 * Created by Aaron on 3/24/2018.
 */
@Singleton
class TacticalMapController @Inject constructor(val gameBoardOperator: GameBoardOperator,
                                                eventNotifier: EventNotifier,
                                                val boardState: BoardState) : EventListener {

    var boardInputState : BoardInputState = BoardInputState.DefaultState

    override fun consumeEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.TileClicked -> playerClickedOnTile(event.tileLocation)
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
        val tilesToHighlight = boardState.getTileLocationsUpToNAway(character.tacMapUnit.movesPerTurn, character.tileLocation)
        gameBoardOperator.highlightTiles(tilesToHighlight, HighlightType.GREEN_TILE)
    }

    private fun moveUnitIfAble(unit: LogicalCharacter, location: TileLocation) {
        if (gameBoardOperator.canUnitMoveTo(location, unit)){
            gameBoardOperator.moveCharacterToTile(unit, location);
        }
    }
}