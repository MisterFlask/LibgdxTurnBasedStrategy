package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector


public class OnFireFunctionalEffect (val damagePerTurn: Int) : FunctionalAttributeEffect() {
    val damageOperator: DamageOperator by lazy{
        GameModuleInjector.generateInstance(DamageOperator::class.java)
    }

    override fun onTurnStart(params: FunctionalEffectParameters) {
        damageOperator.damageCharacter(params.thisCharacter,
                damageAmount = damagePerTurn, abilityAndEquipment = null, sourceCharacter = null)
    }

    companion object {
        val ON_FIRE_ATTRIBUTE  = LogicalCharacterAttribute("On Fire",
                LogicalCharacterAttribute._demonImg.copy(textureId = "6"),
                statusEffect = true,
                otherCustomEffects = listOf(OnFireFunctionalEffect(1)),
                description = {"This unit is on fire and will take one damage per turn until it's put out."})
    }
}