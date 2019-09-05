package com.ironlordbyron.turnbasedstrategy.tileentity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.nearestUnoccupiedSquares
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tacmapunits.WeakMinionSpawner
import com.ironlordbyron.turnbasedstrategy.tacmapunits.actionManager
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.setTrueColor
import com.ironlordbyron.turnbasedstrategy.view.ui.addLabel

private val DIM_RED_COLOR = Color(1f,.5f,.5f, 1f)
public data class CityTileEntity(
        val cityName: String,
        val tileLocation: TileLocation,
        override val actor: Actor,
        var ownedByDemon: Boolean = false) : TileEntity{
    override val tileLocations: Collection<TileLocation>
        get() = listOf(tileLocation)

    val globalTacMapState by LazyInject(GlobalTacMapState::class.java)

    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return false
    }

    companion object{
        val name = "town"
    }

    override val name: String = CityTileEntity.name

    override fun buildUiDisplay() : Table{
        val table = Table()
        table.addLabel("City name: $cityName")
        return table
    }

    fun conquerByDemonAction(){
        this.actor.setTrueColor( DIM_RED_COLOR)
        ownedByDemon = true
        // TODO: City ruins!
        actionManager.addCharacterToTileFromTemplate(
                WeakMinionSpawner(),
                this.tileLocation.nearestUnoccupiedSquares(1).first(),
                playerControlled = false,
                popup = "Gate spawned!")
    }
}

