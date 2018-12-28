package com.ironlordbyron.turnbasedstrategy.common.ruleslisteners

import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WallEntity

/**
 * Handles the rules for when walls or doors are damaged.
 */
public class EntityDamageManager(val eventNotifier: EventNotifier,
                                 val logicalTileTracker: LogicalTileTracker): EventListener{
    override fun consumeGameEvent(event : TacticalGameEvent){
        when(event){
            is TacticalGameEvent.EntityDamage -> handleDamageEvent(event.tileEntity, event.ability)
        }
    }

    private fun handleDamageEvent(tileEntity: TileEntity, logicalAbility: LogicalAbility) {
        when(tileEntity){
            is WallEntity -> damageWall(tileEntity, logicalAbility)
        }
    }

    private fun damageWall(tileEntity: WallEntity, logicalAbility: LogicalAbility) {
        tileEntity.hp -= logicalAbility.damage!!
        if (tileEntity.hp < 0){
            logicalTileTracker.removeEntity(tileEntity)
        }
    }

}