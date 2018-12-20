package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import javax.inject.Inject


class CharacterImageManager @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                                                val spriteActorFactory: SpriteActorFactory,
                                                val logicalTileTracker: LogicalTileTracker) : CanTransformTextureToActor<Actor> {

    override fun placeSprite(tiledMap: TiledMap, tileLocation: TileLocation, texture: TextureRegion): Actor {
        return placeCharacterSprite(tiledMap, tileLocation, texture)
    }

    fun placeCharacterSprite(tiledMap: TiledMap, tileLocation: TileLocation, characterTexture: TextureRegion) : Actor {
        val boundingBox = (tiledMap.layers[0] as TiledMapTileLayer).getBoundsOfTile(tileLocation)
        val characterActor = spriteActorFactory.createSpriteActor(characterTexture, boundingBox)
        return characterActor
    }

    fun retrieveCharacterImage(character: LogicalCharacter) : Actor {
        val tiledTexturePath = character.tacMapUnit.tiledTexturePath;
        val texture = tileMapOperationsHandler.pullGenericTexture(tiledTexturePath.spriteId, tiledTexturePath.tileSetName)
        return Image(texture)
    }
}