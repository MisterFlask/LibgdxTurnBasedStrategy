package com.ironlordbyron.turnbasedstrategy.common.abilities.specific

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbilityEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.DamageType
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.getAttribute
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.isAdjacentTo
import com.ironlordbyron.turnbasedstrategy.toCharacter
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import java.util.*
import kotlin.reflect.KClass


class GuardAction: LogicalAbilityEffect {
    val actionManager = GameModuleInjector.generateInstance(ActionManager::class.java)
    val attributeOperator = GameModuleInjector.generateInstance(AttributeOperator::class.java)
    override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
        attributeOperator.applyAttribute(tileLocationTargeted.getCharacter()!!, GuardedAttribute(characterUsing.id))
    }
}

class GuardedAttribute(val guardedByCharacter: UUID): LogicalCharacterAttribute(
        "Guarded",
        imageIcon = SuperimposedTilemaps.toDefaultProtoActor(),
        id = "GUARDED",
        description = {"if this character is near ${guardedByCharacter.toCharacter().tacMapUnit.templateName}, attacks that target this character hit "},
        customEffects = listOf() // TODO
)

class GuardedFunctionalEffect() : FunctionalAttributeEffect() {
    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    override fun attemptToDamage(thisCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, damageType: DamageType, damage: Int): DamageAttemptResult {
        val guardedAttribute = thisCharacter.getAttribute(GuardedAttribute::class)
        val newTarget = guardedAttribute.guardedByCharacter.toCharacter()
        if (newTarget.isAdjacentTo(thisCharacter)){
            // TODO: This will result in bugs if there are damage modifiers on original character
            return DamageAttemptResult(damage, thisCharacter, newTarget, damage)
        }else{
            return super.attemptToDamage(thisCharacter, logicalAbilityAndEquipment, damageType, damage)
        }
    }
}