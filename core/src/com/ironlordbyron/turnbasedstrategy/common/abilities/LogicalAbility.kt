package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor

public class LogicalAbility(val name: String,
                            val speed: AbilitySpeed,
                            val range: Int,
                            val attackSprite: AbilitySprite? = null,
                            val missionLimit: Int? = null,
                            val damage: Int? = null,
                            val description: String? = null,
                            val abilityClass: AbilityClass,
                            val allowsTargetingSelf: Boolean = false,
                            val requiredTargetType: RequiredTargetType = RequiredTargetType.ANY,
                            val abilityEffects: Collection<LogicalAbilityEffect> = listOf(),
                            val projectileActor: ProtoActor?,
                            val landingActor: ProtoActor?){

}

interface LogicalAbilityEffect {
    public data class SpawnsUnit(val unitToBeSpawned: String): LogicalAbilityEffect
    public class LightsTileOnFire: LogicalAbilityEffect
}

public enum class AbilityClass {
    TARGETED_ABILITY
}
public enum class AbilitySprite(val fileName: String){
    STRIKE("strike")
}
