package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.badlogic.gdx.graphics.g2d.Sprite
import com.ironlordbyron.turnbasedstrategy.controller.RequiredTargetType

public class LogicalAbility(val name: String,
                            val speed: AbilitySpeed,
                            val range: Int,
                            val attackSprite: AbilitySprite? = null,
                            val missionLimit: Int? = null,
                            val damage: Int? = null,
                            val description: String? = null,
                            val abilityClass: AbilityClass,
                            val allowsTargetingSelf: Boolean = false,
                            val requiredTargetType: RequiredTargetType = RequiredTargetType.ANY){

}

public enum class AbilityClass {
    TARGETED_ABILITY
}
public enum class AbilitySprite(val fileName: String){
    STRIKE("strike")
}
