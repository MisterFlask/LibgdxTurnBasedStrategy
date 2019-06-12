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
import javax.inject.Inject
import javax.inject.Singleton

public data class SnoozeLogicalUnitEffect(val alertness: Int = 10) : LogicalUnitEffect{
    override fun toEntry(): Pair<String, Any> {
        return "SNOOZING" to this
    }
}

/**
 * This is the attribute applied to enemy units that are "snoozing" at the beginning of the mission.
 * They will awaken if they are either damaged, OR if a player-controlled unit gets too close.
 */
@Autoinjectable
@Singleton
public class SnoozeFunctionalUnitEffect @Inject constructor(
                                                            override val eventNotifier: EventNotifier,
                                                            val tacMapAlgorithms: TacticalMapAlgorithms,
                                                            val actionManager: ActionManager)
    : FunctionalUnitEffect<SnoozeLogicalUnitEffect>{
    override val id: String = "SNOOZING"
    override val clazz: Class<SnoozeLogicalUnitEffect>
            = SnoozeLogicalUnitEffect::class.java
    // We remove this class when a character moves too close, OR this unit gets struck

    override fun onBeingStruck(logicalAttr: SnoozeLogicalUnitEffect, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute) {
        eventNotifier.notifyListenersOfGameEvent(UnapplyAttributeEvent(thisCharacter, logicalCharacterAttribute))
        onUnapply(logicalAttr, thisCharacter, logicalCharacterAttribute)
    }

    override fun onTurnStart(logicalAttr: SnoozeLogicalUnitEffect, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute) {
        val charactersInNTiles = tacMapAlgorithms.getCharactersWithinNumberOfTilesOfCharacter(logicalAttr.alertness + logicalCharacterAttribute.stacks, thisCharacter)
        if (charactersInNTiles.filter{it.playerAlly}.isNotEmpty()){
            println("Removing snoozing attribute")
            eventNotifier.notifyListenersOfGameEvent(UnapplyAttributeEvent(thisCharacter, logicalCharacterAttribute))
            onUnapply(logicalAttr, thisCharacter, logicalCharacterAttribute)
        }else{
            // alertness increases every turn!
            logicalCharacterAttribute.stacks ++
        }
    }

    fun onUnapply(logicalAttr: SnoozeLogicalUnitEffect, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){
        actionManager.risingText("!", thisCharacter.tileLocation)
    }

    override val stopsUnitFromActing: Boolean
        get() = true
}