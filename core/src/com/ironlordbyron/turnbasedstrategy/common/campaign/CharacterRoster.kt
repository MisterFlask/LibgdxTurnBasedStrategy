package com.ironlordbyron.turnbasedstrategy.common.campaign

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import javax.inject.Singleton

@Singleton
class CharacterRoster{
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
    }
}