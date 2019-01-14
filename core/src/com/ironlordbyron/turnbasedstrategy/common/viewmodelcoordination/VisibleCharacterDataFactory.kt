package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.tiledutils.BoundingRectangle
import com.ironlordbyron.turnbasedstrategy.tiledutils.JustificationType
import com.ironlordbyron.turnbasedstrategy.tiledutils.getBoundingBox
import com.ironlordbyron.turnbasedstrategy.tiledutils.setBoundingBox
import com.ironlordbyron.turnbasedstrategy.view.animation.ImageNotRespectingClicks
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.TextUpdateAnimationGenerator
import com.ironlordbyron.turnbasedstrategy.view.ui.mySkin
import javax.inject.Inject

public class VisibleCharacterDataFactory @Inject constructor (val animationActionQueueProvider: AnimationActionQueueProvider,
                                                              val textUpdateAnimationGenerator: TextUpdateAnimationGenerator){

    val NUM_BOTTOM_TIERS = 1

    public fun generateCharacterHpMarker(character: LogicalCharacter){
        val skin: Skin? = mySkin
        // todo: we can do a high-contrast drop shadow by drawing text, then just drawing it slightly smaller
        val hpMarker = Label("HP", skin)
        hpMarker.fontScaleX = .2f
        hpMarker.fontScaleY = .2f
        hpMarker.setBoundingBox(character.actor.getBoundingBox().getChunkOfBoundingRectangle(NUM_BOTTOM_TIERS, JustificationType.BOTTOM, 0))
        character.actor.addActor(hpMarker)
        character.actor.hpMarker = hpMarker
    }

    public fun updateCharacterHpMarkerInSequence(character: LogicalCharacter){
        animationActionQueueProvider.addAction(textUpdateAnimationGenerator.generateActorActionPair(character.actor.hpMarker!!, character.healthLeft.toString(), character.actor))
    }


}