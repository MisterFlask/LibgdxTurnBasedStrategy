package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation


@SpawnableUnitTemplate("WEAK_MINION_SPAWNER")
public fun WeakMinionSpawner()
    : TacMapUnitTemplate
{
    return TacMapUnitTemplate(0,
        TacMapUnitTemplate._demonImg.copy(textureId = "8"),
        templateName = "EnemySpawner",
        abilities = listOf(SpawnWeakMinionAbility()),
        enemyAiType = EnemyAiType.IMMOBILE_UNIT,
        walkableTerrainTypes = listOf())
}

public fun SpawnWeakMinionAbility() : LogicalAbility
{
    return LogicalAbility(
            "Spawn Unit",
            AbilitySpeed.ENDS_TURN,
            damage = null,
            range = 2,
            description = "Spawns a weak minion.  1-turn cooldown.",
            abilityClass = AbilityClass.TARGETED_ATTACK_ABILITY,
            requiredTargetType = RequiredTargetType.NO_CHARACTER_AT_LOCATION,
            abilityEffects = listOf(SpawnUnitDynamicallyEffect({WeakSlime()}, 2)),
            projectileActor = null,
            landingActor = DataDrivenOnePageAnimation.EXPLODE)
}

public data class SpawnUnitDynamicallyEffect(val unitToBeSpawned:  () -> TacMapUnitTemplate,
                                             val maxCooldown: Int): LogicalAbilityEffect{
    val unitSpawner = GameModuleInjector.generateInstance(ActionManager::class.java)
    val animationActionQueueProvider = GameModuleInjector.generateInstance(AnimationActionQueueProvider::class.java)
    var cooldown: Int = maxCooldown

    override fun runAction(characterUsing: LogicalCharacter,
                           tileLocationTargeted: TileLocation) {
        // todo; generalize cooldown
        cooldown--
        if (cooldown == 0){
            unitSpawner.addCharacterToTileFromTemplate(unitToBeSpawned(), tileLocationTargeted,
                characterUsing.playerControlled)
            cooldown = maxCooldown
        }

    }
}


