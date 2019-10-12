package com.ironlordbyron.turnbasedstrategy.common.abilities.specific

import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.*


val BackstabAbility = LogicalAbility("Guard", AbilitySpeed.FREE_ACTION, 1,
        description= "Until your next turn, when the targeted ally is attacked," +
                "this character receives damage instead if it is adjacent.  " +
                "Works once per turn.",
        requiredTargetType = RequiredTargetType.ALLY_ONLY,
        abilityEffects = listOf(GuardAction()),
        abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
        allowsTargetingSelf = false,
        requiresTarget = true,
        rangeStyle = RangeStyle.Simple(2),
        projectileActor = null,
        landingActor = null,
        intentType = IntentType.OTHER,
        damageStyle = BackstabDamageStyle(1, 4)
)

class BackstabDamageStyle(val baseDamage: Int, val additionalBackstabDamage: Int): DamageStyle{
    override fun getDamageAmount(characterUsing: LogicalCharacter,
                                 logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                                 targetCharacter: LogicalCharacter): Int {
        if (targetIsFlanked(characterUsing, targetCharacter)){
            return baseDamage + additionalBackstabDamage
        }else{
            return baseDamage
        }
    }

    private fun targetIsFlanked(characterUsing: LogicalCharacter, targetCharacter: LogicalCharacter): Boolean {
        val difference = targetCharacter.tileLocation - characterUsing.tileLocation
        val locationToCheck = targetCharacter.tileLocation + difference
        val characterAtLocation = locationToCheck.getCharacter()
        if (characterAtLocation != null && characterUsing.isAlliedTo(targetCharacter)){
            return true
        }
        return false
    }

}

public fun LogicalCharacter.isAlliedTo(targetCharacter: LogicalCharacter): Boolean {
    return !(targetCharacter.playerControlled xor this.playerControlled)
}
