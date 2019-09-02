package com.ironlordbyron.turnbasedstrategy.common.campaign

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.tacmapunits.SimpleCityConquerer
import com.ironlordbyron.turnbasedstrategy.tacmapunits.WeakSlime
import com.ironlordbyron.turnbasedstrategy.tacmapunits.classes.startingMelterPistol
import com.ironlordbyron.turnbasedstrategy.tacmapunits.equipment.Shotgun
import com.ironlordbyron.turnbasedstrategy.tacmapunits.equipment.glaive
import javax.inject.Singleton

interface Quantity{

    class Single: Quantity{
        override fun toString(): String {
            return "(1x)"
        }
    }
    class Infinite: Quantity{
        override fun toString(): String {
            return "(unlimited)"
        }
    }
}

data class EquipmentWithQuantity(val equipment: LogicalEquipment,val quantity: Quantity)

@Singleton
class CharacterAndEquipmentRoster{


    val characters = ArrayList<TacMapUnitTemplate>()
    val equipment = ArrayList<EquipmentWithQuantity>()

    val unusedEquipment: List<EquipmentWithQuantity>
        get() {
            val used = characters.flatMap { it.equipment }
            return equipment
                    .filter{ !used.contains(it.equipment) || it.quantity is Quantity.Infinite }
        }

    init {
        characters.addAll(listOf(TacMapUnitTemplate.RANGED_ENEMY, SimpleCityConquerer(), WeakSlime()))
        equipment.add(EquipmentWithQuantity(startingMelterPistol(), Quantity.Infinite()))
        equipment.add(EquipmentWithQuantity(glaive(), Quantity.Infinite()))
        equipment.add(EquipmentWithQuantity(Shotgun(), Quantity.Infinite()))
    }

    fun attachEquipmentToCharacter(equipment: LogicalEquipment, characterToEquip: TacMapUnitTemplate){
        for (cha in characters){
            if (cha.equipment.map{it.uuid}.contains(equipment.uuid)){
                cha.equipment.removeIf{it.uuid == equipment.uuid}
            }
        }
        characterToEquip.equipment.add(equipment)
    }
}