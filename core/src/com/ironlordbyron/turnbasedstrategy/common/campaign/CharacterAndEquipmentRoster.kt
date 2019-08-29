package com.ironlordbyron.turnbasedstrategy.common.campaign

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.tacmapunits.classes.startingMelterPistol
import javax.inject.Singleton

@Singleton
class CharacterAndEquipmentRoster{
    val characters = ArrayList<TacMapUnitTemplate>()
    val equipment = ArrayList<LogicalEquipment>()

    val unusedEquipment: List<LogicalEquipment>
        get() {
            val used = characters.flatMap { it.equipment }
            return equipment.filter{!used.contains(it)}
        }

    init {
        //todo: better
        characters.addAll(listOf(TacMapUnitTemplate.RANGED_ENEMY))
        equipment.add(startingMelterPistol())
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