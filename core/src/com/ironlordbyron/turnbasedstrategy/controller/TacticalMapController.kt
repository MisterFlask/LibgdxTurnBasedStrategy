package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.ai.EnemyTurnRunner
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import javax.inject.Inject
import javax.inject.Singleton

interface BoardInputState{
    abstract val name: String
    data class PlayerIsPlacingUnits(val unitsToPlace: ArrayList<TacMapUnitTemplate>, override val name: String = "PlacingUnits"): BoardInputState{
        fun nextUnit() : TacMapUnitTemplate?
        {
            return unitsToPlace.firstOrNull()
        }
    }
    data class UnitSelected(val unit: LogicalCharacter, override val name: String = "UnitSelected") : BoardInputState
    data class DefaultState(override val name: String = "DefaultState") : BoardInputState
    data class PlayerIntendsToUseAbility(val unit: LogicalCharacter, val ability: LogicalAbilityAndEquipment, override val name: String = "PlayerIntendsToUseAbility"):BoardInputState
}

/**
 *  Responsible for handling player inputs.
 */
@Singleton
class TacticalMapController @Inject constructor(val gameBoardOperator: GameBoardOperator,
                                                val eventNotifier: EventNotifier,
                                                val boardState: TacticalMapState,
                                                val entitySpawner: EntitySpawner,
                                                val abilityController: AbilityController,
                                                val mapHighlighter: MapHighlighter,
                                                val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                val enemyTurnRunner: EnemyTurnRunner,
                                                val animationActionQueueProvider: AnimationActionQueueProvider) : EventListener, BoardInputStateProvider {

    override var boardInputState : BoardInputState = BoardInputState.DefaultState()
        set(value) {
            println("Setting board input state: $value")
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.SwitchedGuiState(value))
            field = value
        }

    var selectedCharacter: LogicalCharacter? = null

    var playerHasPriority = true
    override fun consumeGuiEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.TileClicked -> {
                if (playerHasPriority) playerClickedOnTile(event.tileLocation)
            }
            is TacticalGuiEvent.EndTurnButtonClicked -> {
                if (playerHasPriority) {
                    playerHasPriority = false
                    mapHighlighter.killHighlights()
                    enemyTurnRunner.endTurn()
                }
            }
            is TacticalGuiEvent.FinishedEnemyTurn -> {
                playerHasPriority = true
            }
            is TacticalGuiEvent.ClickedButtonToActivateAbility -> {
                val selectedCharacter = selectedCharacter
                if (selectedCharacter == null){
                    throw IllegalStateException("Clicked button to activate abilityEquipmentPair when no char selected")
                }
                boardInputState = BoardInputState.PlayerIntendsToUseAbility(selectedCharacter,
                        event.abilityEquipmentPair)
                if (event.abilityEquipmentPair.ability.abilityClass == AbilityClass.TARGETED_ABILITY){
                    abilityController.signalIntentToActOnAbility(selectedCharacter, event.abilityEquipmentPair)
                }
            }
            is TacticalGuiEvent.ScenarioStart -> {
                boardInputState = BoardInputState.PlayerIsPlacingUnits(arrayListOf(TacMapUnitTemplate.DEFAULT_UNIT,
                        TacMapUnitTemplate.DEFAULT_ENEMY_UNIT))
                val boardInputState = boardInputState as BoardInputState.PlayerIsPlacingUnits
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.PlayerIsPlacingUnit(boardInputState.unitsToPlace.first()))
            }

            is TacticalGuiEvent.CycleUnitCarousel -> {
                val boardInputState = this.boardInputState
                if (boardInputState !is BoardInputState.PlayerIsPlacingUnits){
                    return // todo; may be other situations where this is valid
                }
                if (event.characterIdSelected == null){
                    // just move to the next character
                    val tmp = boardInputState.unitsToPlace[0]

                    // cycle first to the end
                    boardInputState.unitsToPlace.removeAt(0)
                    boardInputState.unitsToPlace.add(tmp)
                }else{
                    throw NotImplementedError("Haven't yet done cycling to specific unit")
                }
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.PlayerIsPlacingUnit(boardInputState.unitsToPlace.first()))

            }
        }
    }

    init{
        eventNotifier.registerGuiListener(this)
    }

    fun placePlayerUnit(tileLocation: TileLocation, unit: TacMapUnitTemplate){
        entitySpawner.addCharacterToTileFromTemplate(unit, tileLocation, playerControlled = true)
        animationActionQueueProvider.runThroughActionQueue()

    }

    fun playerClickedOnTile(location: TileLocation){
        if (boardInputState is BoardInputState.PlayerIsPlacingUnits){
            val boardInputState = boardInputState as BoardInputState.PlayerIsPlacingUnits
            val characterToPlace = boardInputState.unitsToPlace.first()
            boardInputState.unitsToPlace.removeAt(0)

            placePlayerUnit(location, characterToPlace)
            if (boardInputState.unitsToPlace.isEmpty()){
                // todo: graphical showing that the input state has changed
                this.boardInputState = BoardInputState.DefaultState()
            }
            return
        }

        val character = tacticalMapAlgorithms.getCharacterAtLocation(location)
        mapHighlighter.killHighlights()
        val currentBoardInputState = boardInputState
        if (currentBoardInputState is BoardInputState.PlayerIntendsToUseAbility){
            if (abilityController.canUseAbilityOnSquare(currentBoardInputState.unit, currentBoardInputState.ability, character, location)){
                abilityController.useAbility(currentBoardInputState.unit, currentBoardInputState.ability, character, location)
                boardInputState = BoardInputState.DefaultState()
            }else{
                boardInputState = BoardInputState.DefaultState()
            }
            return
        }

        if (character != null){
            selectCharacterInTacMap(character)
            selectedCharacter = character
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.CharacterSelected(character))
        }else{
            val currentBoardInputState = boardInputState
            when(currentBoardInputState){
                is BoardInputState.UnitSelected ->  moveUnitIfAble(currentBoardInputState.unit, location)
            }
            boardInputState = BoardInputState.DefaultState()
            selectedCharacter = null
            eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.CharacterUnselected())
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