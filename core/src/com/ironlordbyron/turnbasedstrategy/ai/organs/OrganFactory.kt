package com.ironlordbyron.turnbasedstrategy.ai.organs

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import tiled.core.Tile

public class OrganFactory{
    fun createTileEntityFromProperties(location: TileLocation, objctProperties: Map<String, String>) : TileEntity? {
        val type = objctProperties["type"]
        when(type){
            "haunting_organ" -> return createHauntingOrgan(location)
            "master_organ" -> return createMasterOrgan(location)
            "shielding_organ" -> return createShieldingOrgan(location)
        }
        return null
    }

    private fun createShieldingOrgan(location: TileLocation): TileEntity? {
        return ShieldingOrgan(location, MasterOrgan.protoActor.toActor(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER),
                "Shielding Organ", organDefended = null)
    }

    private fun createMasterOrgan(location: TileLocation): TileEntity {
        return MasterOrgan(location, MasterOrgan.protoActor.toActor(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER),
                "Master Organ")
    }


    fun createHauntingOrgan(tileLocation: TileLocation) : TileEntity {
        return HauntingOrgan(tileLocation, HauntingOrgan.protoActor.toActor(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER),
                "Haunting Organ")
    }

}