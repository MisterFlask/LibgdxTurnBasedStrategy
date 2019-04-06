package com.ironlordbyron.turnbasedstrategy.common.campaign

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

public data class CharacterClass(val startingHp: Int,
                                 val protoActor: ProtoActor,
                                 val movesPerTurn: Int,
                                 val name: String,
                                 val startingAttributes: ArrayList<LogicalCharacterAttribute> = ArrayList(),
                                 val allowedEquipment: Collection<EquipmentClass> = arrayListOf(EquipmentClass.MELEE_WEAPON_LARGE),
                                 val startingAbilities: Collection<LogicalAbility> = arrayListOf()) {

    companion object {
        val FIGHTER = CharacterClass(startingHp = 5,
                protoActor = SuperimposedTilemaps(tileSetNames = SuperimposedTilemaps.PLAYER_TILE_SETS,
                        textureId = "56"),
                allowedEquipment = listOf(EquipmentClass.MELEE_WEAPON_LARGE),
                movesPerTurn = 5,
                name = "Knight",
                startingAbilities = arrayListOf())
        val ROGUE = CharacterClass(startingHp = 3,
                protoActor = SuperimposedTilemaps(tileSetNames = SuperimposedTilemaps.PLAYER_TILE_SETS,
                        textureId = "57"),
                allowedEquipment = listOf(EquipmentClass.MELEE_WEAPON_LARGE),
                movesPerTurn = 5,
                startingAbilities = listOf(StandardAbilities.FreeAimingFireball),
                name = "Grenadier")
        val CLERIC = CharacterClass(startingHp  = 4,
                protoActor = SuperimposedTilemaps(tileSetNames = SuperimposedTilemaps.PLAYER_TILE_SETS,
                        textureId = "58"),
                allowedEquipment = listOf(EquipmentClass.MELEE_WEAPON_LARGE),
                startingAbilities = listOf(StandardAbilities.Beatdown),
                movesPerTurn = 5,
                name = "Cleric")

    }
}
object TacMapUnitParty{
    fun getDefaultParty() : Collection<TacMapUnitTemplate>{
        return arrayListOf()
    }
}
