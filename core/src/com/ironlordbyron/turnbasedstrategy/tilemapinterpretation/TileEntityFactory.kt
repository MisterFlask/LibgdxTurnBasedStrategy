package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

public class TileEntityFactory @Inject constructor(val eventNotifier: EventNotifier){

    fun createDoor(tileLocation: TileLocation, actor: Actor) : DoorEntity{
        return DoorEntity(eventNotifier, tileLocation, actor)
    }

    fun createWall(tileLocation: TileLocation, actor: Actor) : WallEntity{
        return WallEntity(eventNotifier, tileLocation, actor)
    }
}