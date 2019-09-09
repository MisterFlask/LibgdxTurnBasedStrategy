package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.entrypoints.log
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.PortalEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity

public class OpenDoorAbilityRequirement() : ContextualAbilityRequirement{

    val logicalTileTracker = GameModuleInjector.generateInstance(LogicalTileTracker::class.java)

    override fun canUseAbility(characterUsing: LogicalCharacter) : Boolean{
        val neighbors = (logicalTileTracker.getNeighbors(characterUsing.tileLocation))
        if (neighbors.filter{logicalTileTracker.isDoor(it)}.isNotEmpty()){
            return true
        }else{
            return false
        }
    }
}

public class EnterPortalAbilityRequirement() : ContextualAbilityRequirement{
    override fun canUseAbility(characterUsing: LogicalCharacter) : Boolean{
        val tileEntity = characterUsing.tileLocation.getTileEntity()
        if (tileEntity != null
                && tileEntity is PortalEntity){
            return true
        }
        return false
    }
}
val logicalTileTracker = GameModuleInjector.generateInstance(LogicalTileTracker::class.java)


private fun TileLocation.getTileEntity(): TileEntity? {
    val entitiesAtTile = logicalTileTracker.getEntitiesAtTile(this)
    if (entitiesAtTile.size > 1){
        throw IllegalStateException("More than one entity at tile")
    }
    return entitiesAtTile.firstOrNull()
}

object ContextualAbilities {


    val OpenDoor = LogicalAbility(
        name = "Open Door",
        speed = AbilitySpeed.FREE_ACTION,
        landingActor = null,
        projectileActor = null,
        abilityEffects = listOf(LogicalAbilityEffect.OpensDoor()),
        range = 2,
        description = "Opens a door.",
        abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
        allowsTargetingSelf = true,
        requiredTargetType = RequiredTargetType.DOOR,
            requirement = OpenDoorAbilityRequirement()
    )

    val EnterPortal = LogicalAbility(
            name = "Enter portal",
            speed = AbilitySpeed.FREE_ACTION,
            landingActor = null,
            projectileActor = null,
            range = 2,
            description ="Enters portal, causing the character to leave the stage",
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY, //todo
            requiredTargetType = RequiredTargetType.CUSTOM_FILTER_ONLY,
            requirement = EnterPortalAbilityRequirement(),
            abilityEffects = listOf(EnterPortalAbilityAction())
    )

    val allContextualAbilities: Collection<LogicalAbility> = listOf(OpenDoor, EnterPortal)
}

class EnterPortalAbilityAction : LogicalAbilityEffect {
    val actionmanager by LazyInject(ActionManager::class.java)

    override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
        log("Character is entering portal!")
        actionmanager.evacuateCharacter(characterUsing)
    }

}
