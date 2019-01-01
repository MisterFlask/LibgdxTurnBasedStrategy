package com.ironlordbyron.turnbasedstrategy.ai.organs

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity

// this defines where demons spawn in.
public class ObsidianPortal(override val tileLocation: TileLocation,
                            override val actor: Actor,
                            override val name: String) : TileEntity{

    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return true
    }

}