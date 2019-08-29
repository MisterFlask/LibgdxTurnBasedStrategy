package com.ironlordbyron.turnbasedstrategy.tiledutils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.LogicalCharacterActorGroup
import com.ironlordbyron.turnbasedstrategy.view.animation.ScaledActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject


class CharacterImageManager @Inject constructor(val tiledMapOperationsHandler: TiledMapOperationsHandler,
                                                val spriteActorFactory: SpriteActorFactory,
                                                val stageProvider: StageProvider,
                                                val tileMapProvider: TileMapProvider) : CanTransformTextureToActor {



    override fun placeCharacterActor(tileLocation: TileLocation, protoActor: ProtoActor) : LogicalCharacterActorGroup {
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        val characterActor = protoActor.toActorWrapper(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER)
        val group = LogicalCharacterActorGroup(characterActor)
        group.addActor(characterActor.actor)
        group.setBoundingBox(boundingBox)
        stageProvider.tiledMapStage.addActor(group)
        return group
    }

    fun retrieveCharacterImage(character: LogicalCharacter) : ActorWrapper {
        return retrieveCharacterTemplateImage(character.tacMapUnit)
    }

    fun retrieveCharacterTemplateImage(characterTemplate: TacMapUnitTemplate) : ActorWrapper{
        val tiledTexturePath = characterTemplate.tiledTexturePath;
        val actorWrapper = tiledTexturePath.toActorWrapper(AnimatedImageParams(true, true, 14f, hittable = true))
        return actorWrapper
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
