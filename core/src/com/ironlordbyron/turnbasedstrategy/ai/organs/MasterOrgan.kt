package com.ironlordbyron.turnbasedstrategy.ai.organs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

/**
 * This represents the victory condition of the tactical map.
 * Destroying this causes the fortress to self-destruct.
 */
public class MasterOrgan(override val tileLocation: TileLocation,
                         override val actor: Actor,
                         override val name: String) : TileEntity{
    var hp = 3
    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return true
    }

    override fun runOnDeath() {
        super.runOnDeath()
    }

    override fun runTurn() {

    }
    companion object {
        val protoActor = HauntingOrgan.protoActor.copy(textureId = "2")
    }

}