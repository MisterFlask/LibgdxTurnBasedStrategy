package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.wrappers.LabelWrapperImpl
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.view.ShaderFactory
import com.ironlordbyron.turnbasedstrategy.view.animation.ImageNotRespectingClicks
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.TextUpdateAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.ui.mySkin
import javax.inject.Inject

public class VisibleCharacterDataFactory @Inject constructor (val animationActionQueueProvider: AnimationActionQueueProvider,
                                                              val textUpdateAnimationGenerator: TextUpdateAnimationGenerator,
                                                              val shaderFactory: ShaderFactory,
                                                              val tiledMapStageProvider: TacticalTiledMapStageProvider){

    val NUM_BOTTOM_TIERS = 1

    init{

    }

    public fun generateCharacterHpMarker(character: LogicalCharacter){
        val skin: Skin = mySkin
        // todo: we can do a high-contrast drop shadow by drawing text, then just drawing it slightly smaller
        val hpMarker = LabelWrapperImpl("${character.healthLeft}", skin)
        val viewport = tiledMapStageProvider.tiledMapStage.getViewport()
        val outlineShader = shaderFactory.generateOutlineShaderOfColor(
                Color.BLACK,
                outlineSize = 2f,
                width = viewport.worldWidth,
                height =  viewport.worldHeight)
        //hpMarker.shader = outlineShader
        hpMarker.fontScaleX = .3f
        hpMarker.fontScaleY = .3f
        hpMarker.setBoundingBox(character.actor.getBoundingBox().getChunkOfBoundingRectangle(NUM_BOTTOM_TIERS, JustificationType.BOTTOM, 0))
        character.actor.addActor(hpMarker)
        character.actor.shadeableActor.shader = outlineShader
        character.actor.hpMarker = hpMarker
    }

    public fun updateCharacterHpMarkerInSequence(character: LogicalCharacter){
        animationActionQueueProvider.addAction(textUpdateAnimationGenerator.generateActorActionPair(character.actor.hpMarker!!, character.healthLeft.toString(), character.actor))
    }


}