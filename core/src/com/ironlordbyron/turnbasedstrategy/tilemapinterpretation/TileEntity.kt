package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


interface TileEntity {
    val tileLocation: TileLocation
    fun damage(ability : LogicalAbility)
    val actor: Actor
    val name: String
    fun targetableByAbility(ability: LogicalAbility): Boolean
}

class DoorEntity(val eventNotifier: EventNotifier,
                 override val tileLocation: TileLocation,
                 override val actor: Actor,
                 override val name: String = "door",
                 val hp: Int = 3) : TileEntity {
    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return true;
    }

    override fun damage(ability: LogicalAbility){
    }

    fun openDoor(){

    }

    companion object {
        val doorEntityProtoActor: ProtoActor = SuperimposedTilemaps(tileSetNames = listOf("Door0", "Door1"), textureId = "0")

    }
}