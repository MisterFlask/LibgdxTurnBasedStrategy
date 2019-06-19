package com.ironlordbyron.turnbasedstrategy.common.abilities.specific

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.UnapplyAttributeEvent
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import javax.inject.Inject
import javax.inject.Singleton

val SNOOZING: LogicalCharacterAttribute = LogicalCharacterAttribute("Unaware",
        LogicalCharacterAttribute._demonImg.copy(textureId="10"),
        statusEffect = true,
        customEffects = listOf(SnoozeFunctionalUnitEffect()),
        description = {"This character is unaware."},
        stackable = false,
        tacticalMapProtoActor = DataDrivenOnePageAnimation.SNOOZE_ACTOR,
        tacticalMapProtoActorOffsetY = 6
)

/**
 * This is the attribute applied to enemy units that are "snoozing" at the beginning of the mission.
 * They will awaken if they are either damaged, OR if a player-controlled unit gets too close.
 */
@Singleton
public class SnoozeFunctionalUnitEffect @Inject constructor()
    : FunctionalAttributeEffect(){

    val tacMapAlgorithms: TacticalMapAlgorithms by lazy {
        GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
    }
    val actionManager: ActionManager by lazy {
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    // We remove this class when a character moves too close, OR this unit gets struck

    override fun onBeingStruck(functionalEffectParameters: FunctionalEffectParameters) {
        eventNotifier.notifyListenersOfGameEvent(UnapplyAttributeEvent(functionalEffectParameters.thisCharacter, functionalEffectParameters.logicalCharacterAttribute))
        onUnapply(functionalEffectParameters.thisCharacter, functionalEffectParameters.logicalCharacterAttribute)
    }

    override fun onTurnStart(functionalEffectParameters: FunctionalEffectParameters) {
        val charactersInNTiles = tacMapAlgorithms.getCharactersWithinNumberOfTilesOfCharacter(10, functionalEffectParameters.thisCharacter)
        if (charactersInNTiles.filter{it.playerAlly}.isNotEmpty()){
            println("Removing snoozing attribute")
            eventNotifier.notifyListenersOfGameEvent(UnapplyAttributeEvent(functionalEffectParameters.thisCharacter, functionalEffectParameters.logicalCharacterAttribute))
            onUnapply(functionalEffectParameters.thisCharacter, functionalEffectParameters.logicalCharacterAttribute)
        }else{
            // alertness increases every turn!
            functionalEffectParameters.thisCharacter.incrementAttribute(functionalEffectParameters.logicalCharacterAttribute, 1)
        }
    }

    fun onUnapply(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){
        actionManager.risingText("!!!", thisCharacter.tileLocation, 2f)
    }

    override val stopsUnitFromActing: Boolean
        get() = true
}