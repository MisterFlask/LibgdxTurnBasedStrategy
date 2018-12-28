package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


data class TacMapUnitTemplate(val movesPerTurn: Int,
                              val tiledTexturePath: ProtoActor,
                              val templateName: String = "Peasant",
                              val abilities: List<LogicalAbility> = listOf(StandardAbilities.MeleeAttack),
                              val walkableTerrainTypes : Collection<TerrainType> = listOf(TerrainType.GRASS),
                              val enemyAiType: EnemyAiType = EnemyAiType.BASIC
) {
    companion object TacMapUnit {
        private val _default_sit = SuperimposedTilemaps(tileSetNames = SuperimposedTilemaps.PLAYER_TILE_SETS,
            tileMapWithTextureName = SuperimposedTilemaps.COMMON_TILE_MAP, textureId = "7")
        val DEFAULT_UNIT = TacMapUnitTemplate(8, _default_sit.copy(textureId = "6"), templateName = "Friendly")
        val DEFAULT_ENEMY_UNIT = TacMapUnitTemplate(8, _default_sit.copy(textureId = "7"), templateName = "Enemy")
        val DEFAULT_ENEMY_UNIT_SPAWNER = TacMapUnitTemplate(0, _default_sit.copy(textureId = "8"), templateName = "EnemySpawner",
                abilities = listOf(StandardAbilities.spawnUnit),
                enemyAiType = EnemyAiType.IMMOBILE_UNIT,
                walkableTerrainTypes = listOf())
    }
}