package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.SpriteColorActorAction
import com.kotcrab.vis.ui.building.utilities.Alignment
import javax.inject.Inject

public class DeathAnimationGenerator @Inject constructor(val logicalTileTracker: LogicalTileTracker,
                                                         val tacticalMapState: TacticalMapState){

    public fun turnCharacterSideways(character: LogicalCharacter) : ActorActionPair {
        val actor = character.actor
        actor.setOrigin(Alignment.CENTER.alignment)
        return ActorActionPair(actor, Actions.rotateTo(90f, .5f),
                secondaryActions = arrayListOf(SpriteColorActorAction.build(character, SpriteColorActorAction.DIM_COLOR)))
    }
}