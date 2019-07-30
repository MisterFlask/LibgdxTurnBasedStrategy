package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.DamageAttemptInput
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

class TemporaryHpAttributeEffect : FunctionalAttributeEffect() {
    val attributeActionManager: AttributeActionManager by lazy{
        GameModuleInjector.generateInstance(AttributeActionManager::class.java)
    }

    override fun applyDamageMods(damageAttemptInput: DamageAttemptInput, params: FunctionalEffectParameters): DamageAttemptInput {
        if (params.stacks > damageAttemptInput.damage){
            attributeActionManager.applyAttribute(params.thisCharacter, params.logicalCharacterAttribute, -1 * damageAttemptInput.damage)
            return damageAttemptInput.copy(damage = 0)
        }
        if (params.stacks < damageAttemptInput.damage){
            val finalDamage = damageAttemptInput.damage - params.stacks
            attributeActionManager.unapplyAttribute(params.thisCharacter, params.logicalCharacterAttribute)
            return damageAttemptInput.copy(damage = finalDamage)
        }
        return damageAttemptInput
    }
}