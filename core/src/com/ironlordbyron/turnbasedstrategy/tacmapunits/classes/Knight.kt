package com.ironlordbyron.turnbasedstrategy.tacmapunits.classes

import com.ironlordbyron.turnbasedstrategy.ai.Intent
import com.ironlordbyron.turnbasedstrategy.ai.goals.AttackGoal
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


class KnightClass() : TacMapUnitClass("Knight",
        "Has the secondary weapon of Longsword, which can damage and engage targets.",
        EquipmentClass.LONGSWORD,
        8,
        1,
        6,
        startingSecondaryWeapon = startingLongsword()
){
    override fun createNewTacMapUnit(): TacMapUnitTemplate {
        return TacMapUnitTemplate.DEFAULT_ENEMY_UNIT
    }
}

fun startingLongsword(): LogicalEquipment {
    return LogicalEquipment("Longsword",
            EquipmentClass.DAGGER,
            abilityEnabled = listOf(slash(), slashAndEngage())
    )
}

fun slash(): LogicalAbility {
    return LogicalAbility("Slash",
            speed = AbilitySpeed.ONE_ACTION,
            damageStyle = SimpleDamageStyle(2),
            rangeStyle = RangeStyle.Simple(1),
            description = "Damages an enemy",
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            requiresTarget = true,
            inflictsStatusAffect = listOf(),
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            range = -1,
            landingActor = null,
            projectileActor = null)
}

fun slashAndEngage(): LogicalAbility {
    return LogicalAbility("Slash and Engage",
            speed = AbilitySpeed.ONE_ACTION,
            damageStyle = SimpleDamageStyle(1),
            rangeStyle = RangeStyle.Simple(1),
            description = "Damages an enemy and forces them to attack this character this turn.",
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            requiresTarget = true,
            inflictsStatusAffect = listOf(),
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            range = -1,
            landingActor = null,
            projectileActor = null,
            abilityEffects = listOf(EngageEffect()))
}

public class EngageEffect: LogicalAbilityEffect{
    val actionManager: ActionManager by LazyInject(ActionManager::class.java)
    override fun runAction(characterUsing: LogicalCharacter,
                           tileLocationTargeted: TileLocation) {
        actionManager.risingText("Target changed!", tileLocationTargeted)
        tileLocationTargeted.getCharacter()!!.intent = Intent.Attack(characterUsing.id)
    }
}