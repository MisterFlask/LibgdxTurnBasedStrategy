package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.graphics.Color
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.view.ShaderFactory
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.TextUpdateAnimationGenerator
import javax.inject.Inject

public class VisibleCharacterDataFactory @Inject constructor (val animationActionQueueProvider: AnimationActionQueueProvider,
                                                              val textUpdateAnimationGenerator: TextUpdateAnimationGenerator,
                                                              val shaderFactory: ShaderFactory,
                                                              val textLabelGenerator: TextLabelGenerator){

    val NUM_BOTTOM_TIERS = 1

    init{

    }

    public fun generateCharacterHpMarker(character: LogicalCharacter){
        val hpMarker = textLabelGenerator.generateGradientLabel("${character.healthLeft}")
        val outlineShaderForCharacter = shaderFactory.generateOutlineShaderOfColor(
                Color.BLACK,
                outlineSize = 1.5f)
        character.actor.addActor(hpMarker.actor)
        // character.clickListeningActor.shadeableActor.shader = outlineShaderForCharacter
        character.actor.hpMarker = hpMarker.label
        hpMarker.actor.setBoundingBox(character.actor.getBoundingBox().getChunkOfBoundingRectangle(1, JustificationType.TOP, 0))
        hpMarker.label.setFontScale(.1f)

    }

    public fun updateCharacterHpMarkerInSequence(character: LogicalCharacter){
        animationActionQueueProvider.addAction(textUpdateAnimationGenerator.generateActorActionPair(character.actor.hpMarker!!, character.healthLeft.toString(), character.actor))
    }


}