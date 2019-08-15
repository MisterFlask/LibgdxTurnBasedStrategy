package com.ironlordbyron.turnbasedstrategy.tacmapunits.classes

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

///Defines the different kinds of unit players are allowed to have.
public abstract class TacMapUnitClass(val name: String,
                                      val description: String,
                                      val secondaryWeaponClass: EquipmentClass,
                                      val startingHp: Int,
                                      val startingArmor: Int,
                                      val startingMovement: Int,
                                      val startingSecondaryWeapon: LogicalEquipment,
                                      val startingMp: Int = 0){

    open fun createNewTacMapUnit(): TacMapUnitTemplate{
        return TacMapUnitTemplate.DEFAULT_ENEMY_UNIT
    }
}



class SelfOnlyRangeStyle: RangeStyle{
    override fun getTargetableTiles(characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?): Collection<TileLocation> {
        return listOf(characterUsing.tileLocation)
    }

}