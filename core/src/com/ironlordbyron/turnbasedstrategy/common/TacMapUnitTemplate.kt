package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TerrainType


data class TacMapUnitTemplate(val movesPerTurn: Int,
                              val tiledTexturePath: TiledTexturePath,
                              val attackRadius: Int = 1,
                              val attackDamage: Int = 1,
                              val templateName: String ="Peasant",
                              val walkableTerrainTypes : Collection<TerrainType> = listOf(TerrainType.GRASS),
                              val enemyAiType: EnemyAiType = EnemyAiType.BASIC  // TODO: Tack on an interface for this.
) {
    companion object TacMapUnit {
        val DEFAULT_UNIT = TacMapUnitTemplate(8, TiledTexturePath("6"), templateName = "Friendly")
        val DEFAULT_ENEMY_UNIT = TacMapUnitTemplate(8, TiledTexturePath("7"), templateName = "Enemy")
    }
}