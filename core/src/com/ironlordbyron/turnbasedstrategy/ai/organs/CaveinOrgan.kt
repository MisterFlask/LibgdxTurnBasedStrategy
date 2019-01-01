package com.ironlordbyron.turnbasedstrategy.ai.organs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity

// This organ causes parts of the fortress to cave in unpredictably when other organs are destroyed.
public class CaveinOrgan(override val tileLocation: TileLocation, override val actor: Actor, override val name: String) : TileEntity {
    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}