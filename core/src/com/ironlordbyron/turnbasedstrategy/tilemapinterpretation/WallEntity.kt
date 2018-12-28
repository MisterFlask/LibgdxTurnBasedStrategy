package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier

/**
 * Represents a wall (as created by the Tiled layer, possibly.)
 */
class WallEntity(
        val eventNotifier: EventNotifier,
        override val tileLocation: TileLocation,
        override val actor: Actor,
        override val name: String = "wall") : TileEntity {
    override fun damage(ability: LogicalAbility) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun targetableByAbility(logicalAbility: LogicalAbility): Boolean {
        return false
    }

}