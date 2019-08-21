package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplateTags
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


@SpawnableUnitTemplate("WEAK_MINION_SPAWNER", tags = arrayOf(SpawnableUnitTemplateTags.ORGAN))
public fun WeakMinionSpawner()
    : TacMapUnitTemplate
{
    return TacMapUnitTemplate(0,
        SuperimposedTilemaps.doorImageNumber("43"),
        templateName = "EnemySpawner",
        turnStartAction = SpawnMinionEachTurnAction("WEAK_SLIME", 1),
        enemyAiType = EnemyAiType.IMMOBILE_UNIT,
        metagoal = NullAiMetaGoal(),
        walkableTerrainTypes = listOf())
}


class SpawnMinionEachTurnAction(val tacMapUnitTemplateId: String,
                                      val numberToSpawn: Int)
    : TurnStartAction(displayName = "Spawn Minion",
        extendedDescription = "Spawns $numberToSpawn $tacMapUnitTemplateId nearby."){
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val attributeActionManager: AttributeActionManager by lazy {
        GameModuleInjector.generateInstance(AttributeActionManager::class.java)
    }

    override fun perform(logicalCharacter: LogicalCharacter) {
        val squaresToSpawn = logicalCharacter.tileLocation.nearestUnoccupiedSquares(this.numberToSpawn)
        for (square in squaresToSpawn){
            actionManager.addCharacterToTileFromTemplate(this.tacMapUnitTemplateId.toTacMapUnitTemplate(), square, playerControlled = false)
        }
    }
}