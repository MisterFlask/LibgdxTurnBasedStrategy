package com.ironlordbyron.turnbasedstrategy.common.abilities.specific

import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.isAdjacentTo
import com.ironlordbyron.turnbasedstrategy.toCharacter
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import java.util.*


class GuardAction: LogicalAbilityEffect {
    val actionManager = GameModuleInjector.generateInstance(ActionManager::class.java)
    val attributeOperator = GameModuleInjector.generateInstance(AttributeActionManager::class.java)
    override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
        attributeOperator.applyAttribute(tileLocationTargeted.getCharacter()!!, GuardedAttribute(characterUsing.id))
    }
}

class GuardedAttribute(val guardedByCharacter: UUID): LogicalCharacterAttribute(
        "Guarded",
        imageIcon = SuperimposedTilemaps.toDefaultProtoActor(),
        id = "GUARDED",
        description = {"if this character is near ${guardedByCharacter.toCharacter().tacMapUnit.templateName}, attacks that target this character hit "},
        customEffects = listOf(GuardedFunctionalEffect(guardedByCharacter)) // TODO
)

val GuardAbility = LogicalAbility("Guard", AbilitySpeed.FREE_ACTION, 1,
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
        intentType = IntentType.DEFEND
)




class GuardedFunctionalEffect(val guardedByCharacter: UUID) : FunctionalAttributeEffect(), GameEventListener {


    var activatedThisTurn: Boolean = false

    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    init{
        eventNotifier.registerGameListener(this)
    }

    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is TacticalGameEvent.UnitTurnStart -> activatedThisTurn = false
        }
    }
    override fun applyDamageModsAsVictim(damageAttemptInput: DamageAttemptInput, params: FunctionalEffectParameters): DamageAttemptInput {
        if (activatedThisTurn){
            return damageAttemptInput
        }
        val newTarget = guardedByCharacter.toCharacter()
        if (newTarget.isAdjacentTo(damageAttemptInput.targetCharacter)){
            // TODO: This will result in bugs if there are damage modifiers on original character
            activatedThisTurn = true
            actionManager.risingText("Damage redirected!", damageAttemptInput.targetCharacter.tileLocation)
            return damageAttemptInput.copy(targetCharacter = newTarget)
        }else{
            return damageAttemptInput
        }
    }
}