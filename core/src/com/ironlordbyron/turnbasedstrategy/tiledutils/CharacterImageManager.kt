package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoAnimation
import javax.inject.Inject


class CharacterImageManager @Inject constructor(val tiledMapOperationsHandler: TiledMapOperationsHandler,
                                                val spriteActorFactory: SpriteActorFactory,
                                                val stageProvider: TacticalTiledMapStageProvider,
                                                val tileMapProvider: TileMapProvider) : CanTransformTextureToActor<Actor> {

    @Deprecated("Use placeCharacterActor instead")
    override fun placeSprite(tiledMap: TiledMap, tileLocation: TileLocation, texture: TextureRegion): Actor {
        return placeCharacterSprite(tiledMap, tileLocation, texture)
    }

    @Deprecated("Use placeCharacterActor instead")
    fun placeCharacterSprite(tiledMap: TiledMap, tileLocation: TileLocation, characterTexture: TextureRegion) : Actor {
        val boundingBox = (tiledMap.layers[0] as TiledMapTileLayer).getBoundsOfTile(tileLocation)
        val characterActor = spriteActorFactory.createSpriteActor(characterTexture, boundingBox)
        return characterActor
    }


    fun placeCharacterActor( tileLocation: TileLocation, protoAnimation: ProtoAnimation) : Actor {
        val boundingBox = (tileMapProvider.tiledMap.layers[0] as TiledMapTileLayer).getBoundsOfTile(tileLocation)
        val characterActor = protoAnimation.toAnimatedImage(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER)
        stageProvider.tiledMapStage.addActor(characterActor)
        return characterActor
    }

    fun retrieveCharacterImage(character: LogicalCharacter) : Actor {
        val tiledTexturePath = character.tacMapUnit.tiledTexturePath;
        val texture = tiledMapOperationsHandler.pullGenericTexture(tiledTexturePath.spriteId, tiledTexturePath.tileSetName)
        return Image(texture)
    }
}