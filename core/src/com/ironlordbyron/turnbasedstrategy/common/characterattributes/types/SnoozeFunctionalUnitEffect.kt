package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.UnapplyAttributeEvent
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the attribute applied to enemy units that are "snoozing" at the beginning of the mission.
 * They will awaken if they are either damaged, OR if a player-controlled unit gets too close.
 */
@Autoinjectable
@Singleton
public class SnoozeFunctionalUnitEffect @Inject constructor()
    : FunctionalUnitEffect(){

    val tacMapAlgorithms: TacticalMapAlgorithms by lazy {
        GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
    }
    val actionManager: ActionManager by lazy {
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    // We remove this class when a character moves too close, OR this unit gets struck

    override fun onBeingStruck(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute) {
        eventNotifier.notifyListenersOfGameEvent(UnapplyAttributeEvent(thisCharacter, logicalCharacterAttribute))
        onUnapply(thisCharacter, logicalCharacterAttribute)
    }

    override fun onTurnStart(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute) {
        val charactersInNTiles = tacMapAlgorithms.getCharactersWithinNumberOfTilesOfCharacter(logicalCharacterAttribute.stacks + 10, thisCharacter)
        if (charactersInNTiles.filter{it.playerAlly}.isNotEmpty()){
            println("Removing snoozing attribute")
            eventNotifier.notifyListenersOfGameEvent(UnapplyAttributeEvent(thisCharacter, logicalCharacterAttribute))
            onUnapply(thisCharacter, logicalCharacterAttribute)
        }else{
            // alertness increases every turn!
            logicalCharacterAttribute.stacks ++
        }
    }

    fun onUnapply(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){
        actionManager.risingText("!!!", thisCharacter.tileLocation, 2f)
    }

    override val stopsUnitFromActing: Boolean
        get() = true
}