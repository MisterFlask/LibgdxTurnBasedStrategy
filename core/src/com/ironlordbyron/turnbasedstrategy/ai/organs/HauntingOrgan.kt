package com.ironlordbyron.turnbasedstrategy.ai.organs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

// This organ spawns creatures around the player that sap their strength.
public class HauntingOrgan(override val tileLocation: TileLocation,
                         override val actor: Actor,
                         override val name: String) : TileEntity {
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
        val protoActor = SuperimposedTilemaps(tileSetNames = listOf("Slime0", "Slime1"), textureId = "1")
    }
}