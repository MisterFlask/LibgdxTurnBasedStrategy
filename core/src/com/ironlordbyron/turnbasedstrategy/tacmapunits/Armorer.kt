package com.ironlordbyron.turnbasedstrategy.tacmapunits

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.TemporaryHpAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.PainterlyIcons
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

@SpawnableUnitTemplate("ARMORER")
public fun Armorer(): TacMapUnitTemplate {
    return TacMapUnitTemplate(0,
            SuperimposedTilemaps.elementalImageNumber("1"),
            templateName = "Hellplate Armorer",
            enemyAiType = EnemyAiType.IMMOBILE_UNIT,
            turnStartAction = ArmorerTurnAction(),
            metagoal = NullAiMetaGoal())
}

class ArmorerTurnAction() : TurnStartAction(displayName = "Armorer",
        extendedDescription = "Grants all enemies 2 temporary HP." +
                "  This bonus stacks."){
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val attributeActionManager: AttributeActionManager by lazy {
        GameModuleInjector.generateInstance(AttributeActionManager::class.java)
    }
    override val maxCooldown = 3

    override fun perform(logicalCharacter: LogicalCharacter) {
        // give all allied units +1 temporary HP
        Logging.DebugGeneral("Performing armorer action!")
        for (ally in tacticalMapState.listOfEnemyCharacters){
            if (!(ally.getAttributes().any{it.logicalAttribute.id == Hellplate().id})){
                attributeActionManager.applyAttribute(ally, Hellplate(), 3,
                        "Armorer action!")
            }
        }
    }
}


fun Hellplate() : LogicalCharacterAttribute{
    return LogicalCharacterAttribute("Hellplate",
            PainterlyIcons.PROTECT_SKY.toProtoActor(1),
            description = {"Soaks up to $it damage"},
            otherCustomEffects = listOf(TemporaryHpAttributeEffect()),
            stackable = true,
            id = "HELLPLATE"
    )
}

abstract class TurnStartAction(val displayName: String,
                               val extendedDescription: String){
    val cooldownDescription: String
        get() = {
            if (specificallyOnAlertnesses.isEmpty()){
                if (maxCooldown == 0){
                    "Activates every turn"
                } else{
                    if (cooldown == 0){
                        "Activates this turn"
                    } else{
                        "Activates in $cooldown turns"
                    }
                }
            } else{
                "Activates at alertness: $specificallyOnAlertnesses"
            }
        }.invoke()

    open val specificallyOnAlertnesses: Collection<Int> = listOf()
    val alertnessesPerformed: ArrayList<Int> = arrayListOf()
    val leftToBePerformed: List<Int>
        get() = specificallyOnAlertnesses.filter{it !in alertnessesPerformed}
    abstract protected fun perform(logicalCharacter: LogicalCharacter)

    open val maxCooldown: Int = 0
    var cooldown: Int

    init{
        cooldown = 0
    }

    val globalTacMapState: GlobalTacMapState by LazyInject(GlobalTacMapState::class.java)

    public fun performAction(logicalCharacter: LogicalCharacter){
        if (specificallyOnAlertnesses.isEmpty()){
            Logging.DebugCombatLogic("${this.displayName} triggers on cooldown of ${this.maxCooldown} turns.")
            cooldownTriggerLogic(logicalCharacter)
        }else{
            Logging.DebugCombatLogic("${this.displayName} triggers on alertness.")
            alertnessTriggerLogic(logicalCharacter)
        }
    }

    private fun alertnessTriggerLogic(logicalCharacter: LogicalCharacter) {
        if (leftToBePerformed.any { it < globalTacMapState.alertness }) {
            alertnessesPerformed.add(leftToBePerformed.min()!!)
            perform(logicalCharacter)
        }
    }

    private fun cooldownTriggerLogic(logicalCharacter: LogicalCharacter) {
        if (cooldown == 0) {
            cooldown = maxCooldown
            perform(logicalCharacter)
        } else {
            cooldown--
        }
    }
}
