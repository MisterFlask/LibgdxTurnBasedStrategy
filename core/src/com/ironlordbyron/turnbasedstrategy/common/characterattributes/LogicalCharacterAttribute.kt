package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

public data class LogicalCharacterAttribute(val name: String,
                                            val imageIcon: ProtoActor,
                                            val attributeType: LogicalCharacterAttributeType){
    companion object {
        val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        val EXPLODES_ON_DEATH = LogicalCharacterAttribute("Explodes On Death",
                _demonImg,
                LogicalCharacterAttributeType.ExplodesOnDeath(3, 4))
        val MASTER_ORGAN = LogicalCharacterAttribute("Master Organ",
                _demonImg.copy(textureId = "3"),
                LogicalCharacterAttributeType.MasterOrgan())
        val SHIELDS_ANOTHER_ORGAN = LogicalCharacterAttribute("Shields Organ",
                _demonImg.copy(textureId = "4"),
                LogicalCharacterAttributeType.ShieldsAnotherOrgan())
    }
}

public interface LogicalCharacterAttributeType{
    // The below are Organ abilities
    data class ExplodesOnDeath(val radius: Int, val damage: Int) : LogicalCharacterAttributeType
    class ShieldsAnotherOrgan: LogicalCharacterAttributeType
    class MasterOrgan: LogicalCharacterAttributeType
    class Organ: LogicalCharacterAttributeType
}



public class ShieldsAnotherOrganFunctionalAttribute(val entitySpawner: EntitySpawner,
                                                    val tacticalMapState: TacticalMapState): FunctionalCharacterAttribute {
    var thisShields : LogicalCharacter? = null
    var shieldActor: Actor? = null
    override fun onDeath(thisCharacter: LogicalCharacter){

    }

    override fun onInitialization(thisCharacter: LogicalCharacter) {

        val masterOrgan = tacticalMapState
                .listOfCharacters
                .filter{character -> character.attributes.any{it.attributeType is LogicalCharacterAttributeType.MasterOrgan }}
                .firstOrNull()
        if (masterOrgan != null){
            val shieldActor = entitySpawner.spawnEntityAtTileInSequence(DataDrivenOnePageAnimation.RED_SHIELD_ACTOR,
                    masterOrgan.tileLocation)
            this.shieldActor = shieldActor
            thisShields = masterOrgan
        }
    }

}

// These represent modifiers to characters that result in things happening on triggers.
public interface FunctionalCharacterAttribute{
    // defines a function to be run on death.
    fun onDeath(thisCharacter: LogicalCharacter){

    }

    // Defines a function to be run on the start of the enemy (as opposed to player) turn.
    fun onEnemyTurnStart(thisCharacter: LogicalCharacter){

    }

    // this is run after all items have been placed on the map, but BEFORE the first turn is taken.
    fun onInitialization(thisCharacter: LogicalCharacter){

    }
}