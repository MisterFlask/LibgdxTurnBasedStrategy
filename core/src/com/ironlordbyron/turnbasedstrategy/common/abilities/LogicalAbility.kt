package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.badlogic.gdx.graphics.g2d.Sprite

public class LogicalAbility(val name: String,
                            val speed: AbilitySpeed,
                            val range: RangeValue,
                            val attackSprite: AbilitySprite? = null,
                            val missionLimit: Int? = null,
                            val damage: Int? = null,
                            val description: String? = null){

}

public sealed class RangeValue{
    class MeleeAttack() : RangeValue()
    class RangedAttack(val range: Int) : RangeValue()
}
public enum class AbilitySprite(val fileName: String){
    STRIKE("strike")
}
