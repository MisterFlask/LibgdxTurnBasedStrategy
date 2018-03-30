package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates.CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject
import javax.inject.Singleton



data class TacMapUnitTemplate(val movesPerTurn: Int, val tiledTexturePath: TiledTexturePath) {
    companion object TacMapUnit {
        val DEFAULT_UNIT = TacMapUnitTemplate(8, TiledTexturePath("6"))
    }
}


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
                                            val stageProvider: TacticalTiledMapStageProvider,
                                            val logicalTileTracker: LogicalTileTracker,
                                            val imageActorFactory: SpriteActorFactory,
                                            val boardState: BoardState) {

    private val listOfHighlights = ArrayList<Actor>()

    fun moveCharacterToTile(character: LogicalCharacter, toTile: TileLocation) {
        character.tileLocation = toTile
        val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(toTile)
        character.actor.addAction(Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), .5f));
    }

    fun removeCharacter(character: LogicalCharacter) {
        boardState.listOfCharacters.remove(character)
        character.actor.remove()
    }

    fun addCharacterToTile(tacMapUnit: TacMapUnitTemplate, tileLocation: TileLocation) {
        val actor = characterImageManager.placeCharacterSprite(tileMapProvider.tiledMap, tileLocation,
                tileMapOperationsHandler.pullTextureFromTilemap(CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE, tacMapUnit.tiledTexturePath.spriteId, tacMapUnit.tiledTexturePath.tileSetName))
        boardState.listOfCharacters.add(LogicalCharacter(actor, tileLocation, TacMapUnitTemplate.DEFAULT_UNIT))

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

    fun canUnitMoveTo(location: TileLocation, unit: LogicalCharacter): Boolean {
        return true // TODO
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
