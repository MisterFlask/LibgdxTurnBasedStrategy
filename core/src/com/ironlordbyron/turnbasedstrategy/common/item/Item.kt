package com.ironlordbyron.turnbasedstrategy.common.item

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileProtoEntity

public class EquipmentTileEntity(
        val item: LogicalEquipment,
        override val tileLocations: Collection<TileLocation>,
        override val actor: Actor,
        override val name: String) : TileEntity{

}

public class EquipmentTileProtoEntity(val item: LogicalEquipment) : TileProtoEntity<EquipmentTileEntity>{
    override fun toTileEntity(tileLocation: TileLocation): EquipmentTileEntity {
        return EquipmentTileEntity(item, listOf(tileLocation), item.protoActor.toActorWrapper().actor, item.name)
    }
}
