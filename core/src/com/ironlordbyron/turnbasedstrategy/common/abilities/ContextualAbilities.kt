package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import javax.naming.Context

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

object ContextualAbilities {

    val OpenDoor = LogicalAbility(
        name = "Open Door",
        speed = AbilitySpeed.FREE_ACTION,
        landingActor = null,
        projectileActor = null,
        abilityEffects = listOf(LogicalAbilityEffect.OpensDoor()),
        range = 2,
        description = "Opens a door.",
        abilityClass = AbilityClass.TARGETED_ABILITY,
        requiredTargetType = RequiredTargetType.DOOR,
            requirement = OpenDoorAbilityRequirement()
    )

    val EnterPortal = LogicalAbility(
            name = "Enter portal",
            speed = AbilitySpeed.FREE_ACTION,
            landingActor = null,
            projectileActor = null,
            range = 1,
            description ="Enters portal, causing the character to leave the stage",
            abilityClass = AbilityClass.TARGETED_ABILITY, //todo
            requiredTargetType = RequiredTargetType.ANY, //todo
            requirement = EnterPortalAbilityRequirement()
    )

    val allContextualAbilities: Collection<LogicalAbility> = listOf(OpenDoor)
}