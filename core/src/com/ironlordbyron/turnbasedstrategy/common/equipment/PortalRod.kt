package com.ironlordbyron.turnbasedstrategy.common.equipment

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.entity
import com.ironlordbyron.turnbasedstrategy.common.logicalTile
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tacmapunits.classes.nonclassabilities.TileFilter
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WallEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.WarpingInPortalTileProtoEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


fun PortalRod() : LogicalEquipment{
    return LogicalEquipment("Portal Rod", EquipmentClass.UTILITY, listOf(CreatePortalAbility()),
            protoActor = SuperimposedTilemaps.wandImageNumber("2"))
}

fun CreatePortalAbility(): LogicalAbility {
    return LogicalAbility("Create Portal",
            AbilitySpeed.ONE_ACTION,
            3,
            SuperimposedTilemaps.wandImageNumber("2"),
            null,
            null,
            "Creates an egress portal at the targeted square.",
            AbilityClass.TARGETED_ATTACK_ABILITY,
            true,
            abilityEffects = listOf(CreatePortal()),
            landingActor = null,
            projectileActor = null,
            requiresTarget = true,
            requiredTargetType = RequiredTargetType.CUSTOM_FILTER_ONLY,
            abilityUsageTileFilter = NonDoorOrWallTileFilter()
            )
}

class NonDoorOrWallTileFilter : TileFilter {
    override fun tileIsValid(tileLocation: TileLocation): Boolean {
        val entity = tileLocation.entity()
        if (entity == null) return true
        if (entity is DoorEntity) return false
        if (entity is WallEntity) return false
        return true
    }
}

class CreatePortal : LogicalAbilityEffect {
    val actionManager = GameModuleInjector.generateInstance(ActionManager::class.java)
    val animationActionQueueProvider = GameModuleInjector.generateInstance(AnimationActionQueueProvider::class.java)

    override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
        actionManager.createTileEntity(WarpingInPortalTileProtoEntity(), tileLocationTargeted)
    }

}