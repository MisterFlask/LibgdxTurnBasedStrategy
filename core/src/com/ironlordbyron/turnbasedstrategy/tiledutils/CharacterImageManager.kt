package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.TiledTexturePath
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.ScaledActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import java.lang.IllegalStateException
import javax.inject.Inject


class CharacterImageManager @Inject constructor(val tiledMapOperationsHandler: TiledMapOperationsHandler,
                                                val spriteActorFactory: SpriteActorFactory,
                                                val stageProvider: TacticalTiledMapStageProvider,
                                                val tileMapProvider: TileMapProvider) : CanTransformTextureToActor {



    override fun placeCharacterActor(tileLocation: TileLocation, protoActor: ProtoActor) : Actor {
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        val characterActor = protoActor.toActor(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER)
        val group = Group()
        group.addActor(characterActor)
        characterActor.setBoundingBox(boundingBox)
        group.setBoundingBox(boundingBox)
        stageProvider.tiledMapStage.addActor(characterActor)
        return characterActor
    }

    fun retrieveCharacterImage(character: LogicalCharacter) : Actor {
        val tiledTexturePath = character.tacMapUnit.tiledTexturePath;
        return tiledTexturePath.toActor(AnimatedImageParams(true, true, 0.4f))
    }
}

fun Actor.setBoundingBox(boundingBox: BoundingRectangle) {
    if (this is ScaledActor){
        val scaleFactor = this.scalingFactor
        val trueWidth = boundingBox.width.toFloat() * scaleFactor
        val trueHeight = boundingBox.height.toFloat() * scaleFactor
        this.x = boundingBox.x.toFloat() - (trueWidth - boundingBox.width) / 2
        this.y = boundingBox.y.toFloat() - (trueHeight - boundingBox.height) / 2
        this.width = trueWidth
        this.height = trueHeight
    } else{
        this.x = boundingBox.x.toFloat()
        this.y = boundingBox.y.toFloat()
        this.width = boundingBox.width.toFloat()
        this.height = boundingBox.height.toFloat()
    }
}
fun Actor.getBoundingBox(): BoundingRectangle {
    return BoundingRectangle(this.x.toInt(), this.y.toInt(), this.width.toInt(), this.height.toInt())
}
