package com.ironlordbyron.turnbasedstrategy.ai.organs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity

// Shields another organ from damage until it's destroyed.
public class ShieldingOrgan(override val tileLocation: TileLocation,
                            override val actor: Actor,
                            override val name: String,
                            var organDefended: TileEntity?) : TileEntity{

    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}