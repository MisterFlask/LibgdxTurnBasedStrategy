package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplateKeys
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

@SpawnableUnitTemplate("ARMORER")
public fun Armorer(): TacMapUnitTemplate {
    return TacMapUnitTemplate(0,
            TacMapUnitTemplate._demonImg.copy(textureId = "9"),
            templateName = "Master Organ",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            turnStartAction = ArmorerTurnAction())
}


fun bespokeTurn(){

}

class ArmorerTurnAction() : TurnStartAction{
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val attributeOperator: AttributeOperator by lazy {
        GameModuleInjector.generateInstance(AttributeOperator::class.java)
    }

    override fun perform(logicalCharacter: LogicalCharacter) {
        // give all allied units +1 temporary HP
        for (ally in tacticalMapState.listOfEnemyCharacters){

        }
    }
}


interface TurnStartAction{
    fun perform(logicalCharacter: LogicalCharacter)
}
