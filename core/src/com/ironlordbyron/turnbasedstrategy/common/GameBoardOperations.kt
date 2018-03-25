package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.google.inject.Provider
import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates.CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject
import javax.inject.Singleton

class TileMapHighlightActor(val texture: TextureRegion,
                            val stage: TiledMapStage) : Image(texture) {
    init{
        stage.addActor(this)
        color.a = .5f
    }

    fun destroy(){
        this.remove()
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
                                            val stageProvider: TacticalTiledMapStageProvider) {

    private val listOfHighlights = ArrayList<TileMapHighlightActor>()


    private val listOfCharacters = ArrayList<LogicalCharacter>()

    fun addCharacterToTile(character: TiledTexturePath, tileLocation: TileLocation) {
        val actor = characterImageManager.placeCharacterSprite(tileMapProvider.tiledMap, tileLocation,
                tileMapOperationsHandler.pullTextureFromTilemap(CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE, character.spriteId, character.tileSetName))
        listOfCharacters.add(LogicalCharacter(actor, tileLocation))
    }

    fun killHighlights(){
        listOfHighlights.forEach{it.destroy()}
    }

    fun highlightTiles(tiles: Collection<TileLocation>, highlightType: HighlightType){
        val layer = tileMapOperationsHandler.pullTileMapLayer("", MapType.HIGHLIGHT_MAP, TileLayer.TILE_HIGHLIGHT_LAYER_GREEN) //TODO: Improve API
        val texture = tileMapOperationsHandler.pullTextureFromTilemap(CHARACTER_PLACEHOLDER_TILEMAP_TSX_FILE,
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        // TODO: Finish

    }

}

enum class HighlightType(val tiledTexturePath: TiledTexturePath){
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
        val tileSetName: String
)
