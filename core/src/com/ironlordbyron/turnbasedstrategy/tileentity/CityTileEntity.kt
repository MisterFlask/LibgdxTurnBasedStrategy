package com.ironlordbyron.turnbasedstrategy.tileentity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity

private val DIM_RED_COLOR = Color(1f,.5f,.5f, 1f)
public data class CityTileEntity(
        val cityName: String,
        override val tileLocation: TileLocation,
        override val actor: Actor) : TileEntity{

    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return false
    }

    override val name: String = "city"

    fun conquerByDemonAction(){
        this.actor.color = DIM_RED_COLOR
    }
}

