package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType


data class TacMapUnitTemplate(val movesPerTurn: Int,
                              val tiledTexturePath: TiledTexturePath,
                              val templateName: String ="Peasant",
                              val abilities: List<LogicalAbility> = listOf(StandardAbilities.MeleeAttack),
                              val walkableTerrainTypes : Collection<TerrainType> = listOf(TerrainType.GRASS),
                              val enemyAiType: EnemyAiType = EnemyAiType.BASIC
) {
    companion object TacMapUnit {
        val DEFAULT_UNIT = TacMapUnitTemplate(8, TiledTexturePath("6"), templateName = "Friendly")
        val DEFAULT_ENEMY_UNIT = TacMapUnitTemplate(8, TiledTexturePath("7"), templateName = "Enemy")
        val DEFAULT_ENEMY_UNIT_SPAWNER = TacMapUnitTemplate(0, TiledTexturePath("8"), templateName = "EnemySpawner",
                abilities = listOf(StandardAbilities.spawnUnit),
                enemyAiType = EnemyAiType.IMMOBILE_UNIT,
                walkableTerrainTypes = listOf())
    }
}