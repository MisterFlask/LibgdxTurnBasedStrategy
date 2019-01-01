package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.ActorSettable
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


interface TileEntity {
    val tileLocation: TileLocation
    val actor: Actor
    val name: String
    fun targetableByAbility(ability: LogicalAbility): Boolean

    fun init(){

    }

    fun runTurn(){

    }

    fun runOnDeath(){

    }
}

class DoorEntity(val eventNotifier: EventNotifier,
                 override val tileLocation: TileLocation,
                 override var actor: Actor,
                 override val name: String = "door",
                 val hp: Int = 3,
                 val openAnimation: ProtoActor = openDoorProtoActor,
                 val closedAnimation: ProtoActor = closedDoorProtoActor,
                 var isOpen: Boolean = false) : TileEntity, ActorSettable {
    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return true;
    }

    companion object {
        val openDoorProtoActor: ProtoActor = SuperimposedTilemaps(tileSetNames = listOf("Door1"), textureId = "0")
        val closedDoorProtoActor: ProtoActor = SuperimposedTilemaps(tileSetNames = listOf("Door0"), textureId = "0")
        val animatedImageParams = AnimatedImageParams(startsVisible = true)
    }
}