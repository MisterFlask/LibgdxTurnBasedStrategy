package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptInput
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

class TemporaryHpAttributeEffect : FunctionalAttributeEffect() {
    val attributeOperator: AttributeOperator by lazy{
        GameModuleInjector.generateInstance(AttributeOperator::class.java)
    }

    override fun attemptToDamage(damageAttemptInput: DamageAttemptInput, params: FunctionalEffectParameters): DamageAttemptInput {
        if (params.stacks > damageAttemptInput.damage){
            attributeOperator.applyAttribute(params.thisCharacter, params.logicalCharacterAttribute, -1 * damageAttemptInput.damage)
            return damageAttemptInput.copy(damage = 0)
        }
        if (params.stacks < damageAttemptInput.damage){
            val finalDamage = damageAttemptInput.damage - params.stacks
            attributeOperator.unapplyAttribute(params.thisCharacter, params.logicalCharacterAttribute)
            return damageAttemptInput.copy(damage = finalDamage)
        }
        return damageAttemptInput
    }
}