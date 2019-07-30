package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.randomElement
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps


val tacMapState : TacticalMapState by LazyInject(TacticalMapState::class.java)

@SpawnableUnitTemplate("PRISM_FLOWER_CONTROLLER")
public fun PrismFlowerController(): TacMapUnitTemplate {
    return TacMapUnitTemplate(0,
            TacMapUnitTemplate._demonImg.copy(textureId = "9"),
            templateName = "Prism Flower Controller",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            turnStartAction = GenerateUnitAtRandomLocationTurnAction("PRISM_FLOWER", 1, "Prism Flower Sprouting"),
            metagoal = NullAiMetaGoal(),
            attributes = arrayListOf(LogicalCharacterAttribute(
                    "Prism Flower Controller",
                    imageIcon = SuperimposedTilemaps.toDefaultProtoActor(),
                    customEffects = listOf(KillAllUnitsOfTypeEffect("PRISM_FLOWER")),
                    description = {"All prism flowers die when this does."},
                    stackable = false
            )),
            templateId = "PRISM_FLOWER_CONTROLLER")
}

@SpawnableUnitTemplate("FLAME_TOWER_CONTROLLER")
public fun FlameTowerController(): TacMapUnitTemplate {
    return TacMapUnitTemplate(0,
            TacMapUnitTemplate._demonImg.copy(textureId = "9"),
            templateName = "Prism Flower Controller",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            turnStartAction = GenerateUnitAtRandomLocationTurnAction("FLAME_TOWER", 2, "Constructing Flame Tower"),
            metagoal = NullAiMetaGoal(),
            attributes = arrayListOf(LogicalCharacterAttribute(
                    "Flame Tower Controller",
                    imageIcon = SuperimposedTilemaps.toDefaultProtoActor(),
                    customEffects = listOf(KillAllUnitsOfTypeEffect("FLAME_TOWER")),
                    description = {"All prism flowers die when this does."},
                    stackable = false
            )),
            templateId = "FLAME_TOWER_CONTROLLER")
}


class KillAllUnitsOfTypeEffect(val templateId: String) : FunctionalAttributeEffect() {
    override fun onDeath(params: FunctionalEffectParameters) {
        tacMapState.listOfEnemyCharacters
                .filter { it.tacMapUnit.templateId == templateId}
                .forEach{it.killAndDespawn()}
    }
}

class GenerateUnitAtRandomLocationTurnAction(val unitTemplateId: String,
                                             val numUnits: Int = 1,
                                             val name: String): TurnStartAction(name,
        "Creates a prism flower somewhere on the map.") {
    override fun perform(logicalCharacter: LogicalCharacter) {
        for (i in 0 .. numUnits){
            actionManager.addCharacterToTileFromTemplate(
                    unitTemplateId.toTacMapUnitTemplate(),
                    getSpawnableUnitLocation(),
                    playerControlled = false,
                    popup = this.displayName)
        }
    }

    private fun getSpawnableUnitLocation(): TileLocation {
        val tiles = tiledMapProvider.getSpawnableTilemapTiles()
        return tiles.randomElement()
    }

}

