package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor

public class LogicalAbility(val name: String,
                            val speed: AbilitySpeed,
                            val range: Int,
                            val attackSprite: ProtoActor? = null,
                            val missionLimit: Int? = null,
                            val damage: Int? = null,
                            val description: String? = null,
                            val abilityClass: AbilityClass,
                            val allowsTargetingSelf: Boolean = false,
                            val requiredTargetType: RequiredTargetType = RequiredTargetType.ANY,
                            val abilityEffects: Collection<LogicalAbilityEffect> = listOf(),
                            // if this is non-null, there will be a projectile animation.
                            val projectileActor: ProtoActor?,
                            // this is the actor that is spawned when the projectile lands. (Like: a fireball projectile
                            // could result in a langingActor being an explosion.
                            // a projectileActor is NOT required for this to function.
                            val landingActor: ProtoActor?,
                            // this is specifically for contextual abilities, like opening doors.
                            val context: ContextualAbilityParams? = null,
                            val inflictsStatusAffect: LogicalCharacterAttribute? = null){

}

interface LogicalAbilityEffect {
    public data class SpawnsUnit(val unitToBeSpawned: String): LogicalAbilityEffect
    public class LightsTileOnFire: LogicalAbilityEffect
    class OpensDoor: LogicalAbilityEffect
}

public enum class AbilityClass {
    TARGETED_ABILITY
}
