package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplateKeys
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.TemporaryHpAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

@SpawnableUnitTemplate("ARMORER")
public fun Armorer(): TacMapUnitTemplate {
    return TacMapUnitTemplate(0,
            TacMapUnitTemplate._demonImg.copy(textureId = "9"),
            templateName = "Master Organ",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            turnStartAction = ArmorerTurnAction(),
            metagoal = NullAiMetaGoal())
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
            if (!(ally.getAttributes().any{it.logicalAttribute.id == Hellplate().id})){
                attributeOperator.applyAttribute(ally, Hellplate())
            }
        }
    }
}


fun Hellplate() : LogicalCharacterAttribute{
    return LogicalCharacterAttribute("Hellplate",
            SuperimposedTilemaps.toDefaultProtoActor(),
            description = {"Soaks up to $it damage"},
            customEffects = listOf(TemporaryHpAttributeEffect())
    )
}

interface TurnStartAction{
    fun perform(logicalCharacter: LogicalCharacter)
}
