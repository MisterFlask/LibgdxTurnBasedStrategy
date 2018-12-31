package com.ironlordbyron.turnbasedstrategy.common.abilities

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
        context = ContextualAbilityParams(requiresDoorNearby = true)
    )
}