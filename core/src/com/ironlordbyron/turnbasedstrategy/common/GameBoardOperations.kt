package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.ai.AiPlannedAction
import com.ironlordbyron.turnbasedstrategy.ai.BasicEnemyAi
import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiFactory
import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates.CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject
import javax.inject.Singleton





/**
 * Responsible for coordinating game-level actions between lower-level actors like the tile map operations handler
 * and the character image processor.
 * Acts as a facade that should not include raw images and such in its interface.
 */
@Singleton
class GameBoardOperator @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                                            val tileMapProvider: TileMapProvider,
                                            val characterImageManager: CharacterImageManager,
                                            val eventNotifier: EventNotifier,
                                            val logicalTileTracker: LogicalTileTracker,
                                            val imageActorFactory: SpriteActorFactory,
                                            val boardState: TacticalMapState,
                                            val enemyAiFactory:EnemyAiFactory) : EventListener {
    override fun consumeEvent(event: TacticalGuiEvent) {

    }

    public fun endTurn() {
        println("End turn clicked.  TODO!")

        runEnemyTurn()
    }

    private fun runEnemyTurn() {
        for (enemyCharacter in boardState.listOfEnemyCharacters) {
            val ai = enemyAiFactory.getEnemyAi(enemyCharacter.tacMapUnit.enemyAiType)
            val nextActions = ai.getNextActions(enemyCharacter);
            for (action in nextActions){
                when(action){
                    is AiPlannedAction.MoveToTile -> moveCharacterToTile(enemyCharacter, action.to, true)
                }
            }
        }
        eventNotifier.notifyListeners(TacticalGuiEvent.FinishedEnemyTurn())
    }

    init{
        eventNotifier.registerListener(this)
    }
    private val listOfHighlights = ArrayList<Actor>()

    var lastActionInQueue: Action? = null

    fun moveCharacterToTile(character: LogicalCharacter, toTile: TileLocation, waitOnQueuedActions: Boolean) {
        character.tileLocation = toTile
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(toTile)
        val moveAction = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f)
        lastActionInQueue
        character.actor.addAction(moveAction);
    }

    fun removeCharacter(character: LogicalCharacter) {
        boardState.listOfCharacters.remove(character)
        character.actor.remove()
    }

    fun addCharacterToTile(tacMapUnit: TacMapUnitTemplate, tileLocation: TileLocation, playerControlled: Boolean) {
        val actor = characterImageManager.placeCharacterSprite(tileMapProvider.tiledMap, tileLocation,
                tileMapOperationsHandler.pullTextureFromTilemap(CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE, tacMapUnit.tiledTexturePath.spriteId, tacMapUnit.tiledTexturePath.tileSetName))
        boardState.listOfCharacters.add(LogicalCharacter(actor, tileLocation, tacMapUnit, playerControlled))

    }

    fun killHighlights() {
        listOfHighlights.forEach { it.remove() }
        listOfHighlights.removeAll{true}
    }

    fun highlightTiles(tiles: Collection<TileLocation>, highlightType: HighlightType) {
        val texture = tileMapOperationsHandler.pullGenericTexture(
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        for (location in tiles) {
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, texture,
                    alpha = .5f)
            val highlightBlinkingAction = highlightBlinking()
            actor.addAction(highlightBlinkingAction)
            listOfHighlights.add(actor)
        }
    }

}

enum class HighlightType(val tiledTexturePath: TiledTexturePath) {
    RED_TILE(TiledTexturePaths.RED_TILE),
    BLUE_TILE(TiledTexturePaths.BLUE_TILE),
    GREEN_TILE(TiledTexturePaths.GREEN_TILE)
}

object TiledTexturePaths {
    val RED_TILE = TiledTexturePath("0", "red_tile")
    val BLUE_TILE = TiledTexturePath("0", "blue_tile")
    val GREEN_TILE = TiledTexturePath("0", "green_tile")
}


/**
 * All tile textures are assumed to be contained within Player0Characters.tmx
 */
data class TiledTexturePath(
        val spriteId: String,
        val tileSetName: String = "Player0" //Default path name
)
