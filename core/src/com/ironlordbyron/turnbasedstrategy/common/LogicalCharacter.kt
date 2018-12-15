package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.SpriteActor
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TerrainType
import org.xguzm.pathfinding.grid.NavigationGridGraphNode

/**
 * Represents a mutable character generated from a template.
 * Has a location, an associated actor, and a
 */
data class LogicalCharacter(val actor: SpriteActor,
                            var tileLocation: TileLocation,
                            val tacMapUnit: TacMapUnitTemplate,
                            val playerControlled: Boolean,
                            var endedTurn: Boolean = false,
                            var actionsLeft: Int = 2,
                            var maxActionsLeft: Int = 2,
                            var heathLeft: Int = 3,
                            var maxHealth: Int = 3){
    val abilities: Collection<LogicalAbility>
    get() = acquireAbilities()

    val playerAlly: Boolean
    get() = playerControlled //TODO: Differentiate if necessary

    private fun acquireAbilities(): Collection<LogicalAbility> {
        return tacMapUnit.abilities
    }
}
