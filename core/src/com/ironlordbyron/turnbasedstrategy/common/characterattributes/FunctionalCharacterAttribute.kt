package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor


@Deprecated("Use FunctionUnitEffect instead")
// These represent modifiers to characters that result in things happening on triggers.
public abstract class FunctionalCharacterAttribute(){
    var actor: ActorWrapper? = null

    // defines a function to be run on death.
    open fun onDeath(thisCharacter: LogicalCharacter){

    }

    // this is run after all items have been placed on the map, but BEFORE the first turn is taken.
    open fun onInitialization(thisCharacter: LogicalCharacter){

    }

    open fun onCharacterTurnStart(thisCharacter: LogicalCharacter){

    }

    // returns the representation of the attribute ON THE TAC MAP.
    fun generateAttributeTacMapRepresentation(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){
        this.actor = getAttributeTacMapRepresentationInternal(thisCharacter, logicalCharacterAttribute)?.toActor()
        if (this.actor != null){

        }
    }


    /**
     * The actual thing the client can override to supply a graphical represetnation of this item on teh tactical map.
     */
    open fun getAttributeTacMapRepresentationInternal(thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): ProtoActor? {
        return null
    }
}