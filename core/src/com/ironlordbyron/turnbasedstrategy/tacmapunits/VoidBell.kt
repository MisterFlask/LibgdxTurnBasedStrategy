package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplateTags
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

@SpawnableUnitTemplate("VOID_BELL", tags = arrayOf(SpawnableUnitTemplateTags.ORGAN))
public fun VoidBell(): TacMapUnitTemplate {
    return TacMapUnitTemplate(0,
            SuperimposedTilemaps.doorImageNumber("47"),
            templateName = "Void Bell Alarm",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            turnStartAction = SpawnMinionOnSpecificTurnAction("WEAK_MINION", 5),
            metagoal = NullAiMetaGoal(),
            templateId = "VOID_BELL")
}

val actionManager: ActionManager by LazyInject(ActionManager::class.java)

class SpawnMinionOnSpecificTurnAction(val tacMapUnitTemplateId: String,
                                      val numberToSpawn: Int)
    : TurnStartAction(displayName = "Spawn Minion",
        extendedDescription = "Spawns $numberToSpawn $tacMapUnitTemplateId nearby when alertness first becomes [5, 10]."){
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val attributeActionManager: AttributeActionManager by lazy {
        GameModuleInjector.generateInstance(AttributeActionManager::class.java)
    }
    override val specificallyOnAlertnesses: Collection<Int>
        get() = listOf(5, 10)

    override fun perform(logicalCharacter: LogicalCharacter) {
        val squaresToSpawn = logicalCharacter.tileLocation.nearestUnoccupiedSquares(this.numberToSpawn)
        for (square in squaresToSpawn){
            actionManager.addCharacterToTileFromTemplate(this.tacMapUnitTemplateId.toTacMapUnitTemplate(), square, playerControlled = false)
        }
    }
}