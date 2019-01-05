package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

/**
 * So, here's how this works: Each attribute corresponds to a visible icon for the player to interact with.
 * it may have ANY of the provided attributes, and new attributes should be added by .
 */
public data class LogicalCharacterAttribute(val name: String,
                                            val imageIcon: ProtoActor,
                                            val explodesOnDeath: LogicalCharacterAttributeTrigger.ExplodesOnDeath? = null,
                                            val shieldsAnotherOrgan: LogicalCharacterAttributeTrigger.ShieldsAnotherOrgan? = null,
                                            val masterOrgan: Boolean = false,
                                            val organ: Boolean = false){
    companion object {
        val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        val EXPLODES_ON_DEATH = LogicalCharacterAttribute("Explodes On Death",
                _demonImg,
                LogicalCharacterAttributeTrigger.ExplodesOnDeath(3, 4))
        val MASTER_ORGAN = LogicalCharacterAttribute("Master Organ",
                _demonImg.copy(textureId = "3"),
                masterOrgan= true)
        val SHIELDS_ANOTHER_ORGAN = LogicalCharacterAttribute("Shields Organ",
                _demonImg.copy(textureId = "4"),
                shieldsAnotherOrgan = LogicalCharacterAttributeTrigger.ShieldsAnotherOrgan())
    }
}

public class FunctionalCharacterAttributeFactory(val entitySpawner: EntitySpawner,
                                                 val tacticalMapState: TacticalMapState){

    fun getFunctionalAttributesForCharacter(logicalCharacter: LogicalCharacter): List<FunctionalCharacterAttribute> {
        return logicalCharacter.attributes.flatMap{getFunctionalAttributesFromLogicalAttribute(it, logicalCharacter)}
    }

    fun getFunctionalAttributesFromLogicalAttribute(logicalAttribute: LogicalCharacterAttribute, character: LogicalCharacter) : Collection<FunctionalCharacterAttribute>{
        val attrsList = ArrayList<FunctionalCharacterAttribute>()
        if (logicalAttribute.shieldsAnotherOrgan != null){
            attrsList.add(ShieldsAnotherOrganFunctionalAttribute(entitySpawner, tacticalMapState))
        }

        return attrsList
    }

}

public interface LogicalCharacterAttributeTrigger{
    // The below are Organ abilities
    data class ExplodesOnDeath(val radius: Int, val damage: Int) : LogicalCharacterAttributeTrigger
    class ShieldsAnotherOrgan: LogicalCharacterAttributeTrigger
    class MasterOrgan: LogicalCharacterAttributeTrigger
    class Organ: LogicalCharacterAttributeTrigger
}



public class ShieldsAnotherOrganFunctionalAttribute(val entitySpawner: EntitySpawner,
                                                    val tacticalMapState: TacticalMapState): FunctionalCharacterAttribute {
    var thisShields : LogicalCharacter? = null
    var shieldActor: Actor? = null

    override fun onDeath(thisCharacter: LogicalCharacter){
        val shieldActor = this.shieldActor
        if (shieldActor != null){
            entitySpawner.despawnEntityInSequence(shieldActor)
        }
    }

    override fun onInitialization(thisCharacter: LogicalCharacter) {

        val masterOrgan = getCharacterWithAttribute(LogicalCharacterAttributeTrigger.MasterOrgan())
        if (masterOrgan != null){
            val shieldActor = entitySpawner.spawnEntityAtTileInSequence(
                    DataDrivenOnePageAnimation.RED_SHIELD_ACTOR,
                    masterOrgan.tileLocation)
            this.shieldActor = shieldActor
            thisShields = masterOrgan
        }
    }

    private fun getCharacterWithAttribute(logicalCharacterAttribute: LogicalCharacterAttributeTrigger): LogicalCharacter? {
        return tacticalMapState
                .listOfCharacters
                .filter { character -> character.attributes.any { it.masterOrgan } }
                .firstOrNull()
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