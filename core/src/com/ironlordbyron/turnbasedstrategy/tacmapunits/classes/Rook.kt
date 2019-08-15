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


class RookClass() : TacMapUnitClass("Rook",
        "Has the secondary weapon of Grappling Hook, which can pull friendlies and enemies toward you.",
        EquipmentClass.HOOK,
        8,
        1,
        6,
        startingSecondaryWeapon = startingHook()
){
    override fun createNewTacMapUnit(): TacMapUnitTemplate {
        return TacMapUnitTemplate.DEFAULT_ENEMY_UNIT
    }
}

fun startingHook(): LogicalEquipment {
    return LogicalEquipment("Hook",
            EquipmentClass.DAGGER,
            abilityEnabled = listOf(distantRescue(), hookWhereItHurts())
    )
}

fun distantRescue(): LogicalAbility {
    return LogicalAbility("Hook Rescue",
            speed = AbilitySpeed.ONE_ACTION,
            damageStyle = SimpleDamageStyle(0),
            rangeStyle = RangeStyle.Simple(5),
            description = "Pulls an ally to a nearby tile",
            requiredTargetType = RequiredTargetType.ALLY_ONLY,
            requiresTarget = true,
            inflictsStatusAffect = listOf(),
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            range = -1,
            landingActor = null,
            abilityEffects = listOf(PullEffect()),
            projectileActor = null)
}

fun hookWhereItHurts(): LogicalAbility {
    return LogicalAbility("Hook Where It Hurts",
            speed = AbilitySpeed.ONE_ACTION,
            damageStyle = SimpleDamageStyle(1),
            rangeStyle = RangeStyle.Simple(5),
            description = "Damages an enemy and pulls them to your tile.",
            requiredTargetType = RequiredTargetType.ENEMY_ONLY,
            requiresTarget = true,
            inflictsStatusAffect = listOf(),
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            range = -1,
            landingActor = null,
            projectileActor = null,
            abilityEffects = listOf(PullEffect()))
}

public class PullEffect: LogicalAbilityEffect{
    val actionManager: ActionManager by LazyInject(ActionManager::class.java)
    override fun runAction(characterUsing: LogicalCharacter,
                           tileLocationTargeted: TileLocation) {
        val targetedCharacter = tileLocationTargeted.getCharacter()!!
        actionManager.risingText("Pulled!", tileLocationTargeted)
        actionManager.moveCharacterToTile(targetedCharacter, characterUsing.tileLocation.nearestUnoccupiedSquares(1)
                .first(), waitOnMoreQueuedActions = true, wasPlayerInitiated = false)//todo: Is this right?

    }
}