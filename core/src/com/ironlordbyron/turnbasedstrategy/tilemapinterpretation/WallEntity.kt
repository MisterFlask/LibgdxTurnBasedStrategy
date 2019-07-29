package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent

/**
 * Represents a wall (as created by the Tiled layer, possibly.)
 */
class WallEntity(
        val eventNotifier: EventNotifier,
        val tileLocation: TileLocation,
        override val actor: Actor,
        override val name: String = "wall") : TileEntity {
    override val tileLocations: Collection<TileLocation>
        get() = listOf(tileLocation)

    override fun targetableByAbility(logicalAbility: LogicalAbility): Boolean {
        return false
    }

}
