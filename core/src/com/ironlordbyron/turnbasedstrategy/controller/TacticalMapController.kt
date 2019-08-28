package com.ironlordbyron.turnbasedstrategy.controller

import com.badlogic.gdx.graphics.Color
import com.google.common.base.Stopwatch
import com.ironlordbyron.turnbasedstrategy.ai.EnemyTurnRunner
import com.ironlordbyron.turnbasedstrategy.ai.Intent
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tacmapunits.tacMapState
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

val tacMapState: TacticalMapState by LazyInject(TacticalMapState::class.java)

interface BoardInputState{
    abstract val name: String
    data class PlayerIsPlacingUnits(override val name: String = "PlacingUnits"): BoardInputState{
        fun nextUnit() : TacMapUnitTemplate?
        {
            return tacMapState.unitsAvailableToDeploy.firstOrNull()
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
                                                val actionManager: ActionManager,
                                                val abilityController: AbilityController,
                                                val mapHighlighter: MapHighlighter,
                                                val tacticalMapAlgorithms: TacticalMapAlgorithms,
                                                val enemyTurnRunner: EnemyTurnRunner,
                                                val animationActionQueueProvider: AnimationActionQueueProvider,
                                                val tileMapHighlighter: MapHighlighter) : EventListener, BoardInputStateProvider {

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
                    val stopwatch = Stopwatch.createStarted()
                    playerHasPriority = false
                    mapHighlighter.killHighlights()
                    enemyTurnRunner.endTurn()
                    stopwatch.stop()
                    println("Ran enemy turn in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)} millis")

                }
            }
            is TacticalGuiEvent.FinishedEnemyTurn -> {

                // reset block a la STS
                com.ironlordbyron.turnbasedstrategy.controller.tacMapState.listOfPlayerCharacters.
                        forEach{it.tacMapUnit.block = 0}

                playerHasPriority = true
            }
            is TacticalGuiEvent.ClickedButtonToActivateAbility -> {
                if (boardInputState is BoardInputState.PlayerIsPlacingUnits){
                    return
                }
                val selectedCharacter = selectedCharacter
                if (selectedCharacter == null){
                    throw IllegalStateException("Clicked button to activate abilityEquipmentPair when no char selected")
                }
                boardInputState = BoardInputState.PlayerIntendsToUseAbility(selectedCharacter,
                        event.abilityEquipmentPair)
                if (event.abilityEquipmentPair.ability.requiresTarget){
                    abilityController.signalIntentToActOnAbility(selectedCharacter, event.abilityEquipmentPair)
                }
                else{
                    abilityController.useAbility(selectedCharacter, event.abilityEquipmentPair, selectedCharacter, selectedCharacter.tileLocation)
                }
            }
            is TacticalGuiEvent.ScenarioStart -> {
                tacMapState.unitsAvailableToDeploy.addAll(event.scenarioParams.unitsThatPlayerWillDeploy)
                boardInputState = BoardInputState.PlayerIsPlacingUnits()
                val boardInputState = boardInputState as BoardInputState.PlayerIsPlacingUnits
                tileMapHighlighter.highlightTiles(tiledMapProvider.getPlayerPlacementTilemapTiles(), HighlightType.GREEN_TILE, tag = "move")
                // TODO:  This breaks in the case where we have zero units
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.PlayerIsPlacingUnit(boardInputState.nextUnit()!!))
            }

            is TacticalGuiEvent.CycleUnitCarousel -> {
                val boardInputState = this.boardInputState
                if (boardInputState !is BoardInputState.PlayerIsPlacingUnits){
                    return // todo; may be other situations where this is valid
                }
                if (event.characterIdSelected == null){
                    // just move to the next character
                    val tmp = tacMapState.unitsAvailableToDeploy[0]

                    // cycle first to the end
                    tacMapState.unitsAvailableToDeploy.removeAt(0)
                    tacMapState.unitsAvailableToDeploy.add(tmp)
                }else{
                    throw NotImplementedError("Haven't yet done cycling to specific unit")
                }
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.PlayerIsPlacingUnit(tacMapState.unitsAvailableToDeploy.first()))

            }
            is TacticalGuiEvent.TileHovered -> {
                val tileTargeted = event.location
                val boardInputState = boardInputState
                if (boardInputState is BoardInputState.PlayerIntendsToUseAbility){
                    val tilesInRange = boardInputState.ability.getSquaresInRangeOfAbility(boardInputState.unit.tileLocation, boardInputState.unit)
                    if (tileTargeted in tilesInRange){
                        val aoe = boardInputState.ability.ability.areaOfEffect.getTilesAffected(
                                tileTargeted,
                                boardInputState.unit,
                                boardInputState.ability)
                        tileMapHighlighter.highlightTiles(aoe, HighlightType.tileOfColor(Color.GOLD), tag = "aoe-highlights")
                    } else {
                        tileMapHighlighter.killHighlights(tag = "aoe-highlights")
                    }
                }
            }
        }
    }

    init{
        eventNotifier.registerGuiListener(this)
    }

    fun placePlayerUnit(tileLocation: TileLocation, unit: TacMapUnitTemplate){
        actionManager.addCharacterToTileFromTemplate(unit, tileLocation, playerControlled = true)
        animationActionQueueProvider.runThroughActionQueue()

    }

    fun playerClickedOnTile(location: TileLocation){
        if (boardInputState is BoardInputState.PlayerIsPlacingUnits){
            if (location.getCharacter() != null){
                return
            }
            if (!isValidPlacementOfCharacter(location)){
                actionManager.createSpeechBubbleAtLocation(location, "I can only deploy in a drop zone!")
                return
            }
            val boardInputState = boardInputState as BoardInputState.PlayerIsPlacingUnits
            val characterToPlace = tacMapState.unitsAvailableToDeploy.first()
            tacMapState.unitsAvailableToDeploy.removeAt(0)

            placePlayerUnit(location, characterToPlace)
            if (boardInputState.nextUnit() != null){
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.PlayerIsPlacingUnit(boardInputState.nextUnit()!!))
            }
            if (tacMapState.unitsAvailableToDeploy.isEmpty()){
                // todo: graphical showing that the input state has changed
                this.boardInputState = BoardInputState.DefaultState()
                eventNotifier.notifyListenersOfGuiEvent(TacticalGuiEvent.CharacterUnselected())
                tileMapHighlighter.killHighlights()
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

    private fun isValidPlacementOfCharacter(location: TileLocation): Boolean {
        val tilesValidForPlacement = tiledMapProvider.getPlayerPlacementTilemapTiles()

        return tilesValidForPlacement.contains(location)
    }

    private fun selectCharacterInTacMap(character: LogicalCharacter) {
        boardInputState = BoardInputState.UnitSelected(character)
        if (character.playerControlled){
            val tilesToHighlight = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(character)
            mapHighlighter.highlightTiles(tilesToHighlight, HighlightType.GREEN_TILE, tag = "move")
        }
        val enemiesTargetingPlayer = boardState.listOfEnemyCharacters
                .filter{it.intent is Intent.Attack}
                .filter{(it.intent as Intent.Attack).logicalCharacterUuid == character.id}
                .map{it.tileLocation}
        mapHighlighter.highlightTiles(enemiesTargetingPlayer, HighlightType.RED_TILE, tag = "attack")

        // actionManager.createSpeechBubbleAtLocation(character, "'Allo!")
        animationActionQueueProvider.runThroughActionQueue()

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