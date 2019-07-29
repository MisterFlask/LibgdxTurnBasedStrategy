package com.ironlordbyron.turnbasedstrategy.tileentity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.setTrueColor

private val DIM_RED_COLOR = Color(1f,.5f,.5f, 1f)
public data class CityTileEntity(
        val cityName: String,
        val tileLocation: TileLocation,
        override val actor: Actor,
        var ownedByDemon: Boolean = false) : TileEntity{
    override val tileLocations: Collection<TileLocation>
        get() = listOf(tileLocation)

    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return false
    }

    companion object{
        val name = "town"
    }

    override val name: String = CityTileEntity.name

    fun conquerByDemonAction(){
        this.actor.setTrueColor( DIM_RED_COLOR)
        ownedByDemon = true
    }
}

