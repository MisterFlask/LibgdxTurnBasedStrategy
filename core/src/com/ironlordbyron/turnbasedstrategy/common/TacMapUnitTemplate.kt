package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities
import com.ironlordbyron.turnbasedstrategy.common.abilities.specific.GuardAbility
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.register
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import java.util.*


data class TacMapUnitTemplate(val movesPerTurn: Int,
                              val tiledTexturePath: ProtoActor,
                              val templateName: String = "Peasant",
                              var abilities: List<LogicalAbility> = listOf(),
                              var allowedEquipment: Collection<EquipmentClass> = listOf(EquipmentClass.MELEE_WEAPON_LARGE), //TODO
                              val walkableTerrainTypes : Collection<TerrainType> = listOf(TerrainType.GRASS),
                              val enemyAiType: EnemyAiType = EnemyAiType.BASIC,
                              var startingAttributes: Collection<LogicalCharacterAttribute> = listOf(),
                              val uuid: UUID = UUID.randomUUID(),
                              var maxActionsLeft: Int = 2,
                              var maxHealth: Int = 3,
                              var healthLeft: Int = maxHealth,
                              var equipment: ArrayList<LogicalEquipment> = ArrayList(),
                              var attributes: ArrayList<LogicalCharacterAttribute> = ArrayList(startingAttributes),
                              val strength: Int = 0,
                              val dexterity: Int = 0,

                              // used in map generation
                              val difficulty: Int = 1,
                              val possibleRandomizedIntents: List<IntentType> = listOf(IntentType.ATTACK, IntentType.MOVE)
) {

    private val stacksOfAttribute: HashMap<String, Int> = hashMapOf()


    init{
        for (attribute in attributes){
            stacksOfAttribute[attribute.id] = 1
        }
    }
    fun getAttributes() : Collection<LogicalCharacter.StacksOfAttribute> {
        val ret = ArrayList<LogicalCharacter.StacksOfAttribute>()
        for (attribute in attributes){
            if (!stacksOfAttribute.containsKey(attribute.id)){
                stacksOfAttribute[attribute.id] = 1
            }
            ret.add(LogicalCharacter.StacksOfAttribute(stacksOfAttribute[attribute.id]!!, attribute))
        }
        return ret
    }


    fun incrementAttribute(logicalAttribute: LogicalCharacterAttribute, stacks: Int){
        if (stacksOfAttribute.containsKey(logicalAttribute.id)){
            stacksOfAttribute[logicalAttribute.id] =  (stacksOfAttribute[logicalAttribute.id]!! + stacks)
        } else{
            attributes.add(logicalAttribute)
            stacksOfAttribute[logicalAttribute.id] = stacks
        }
    }

    // defensive copying
    init{
        attributes = ArrayList(attributes)
        equipment = ArrayList(equipment)
        abilities = ArrayList(abilities)
        allowedEquipment = ArrayList(allowedEquipment)
        startingAttributes = ArrayList(startingAttributes)
    }
    companion object TacMapUnit {
        private val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        private val _default_sit = SuperimposedTilemaps(tileSetNames = SuperimposedTilemaps.PLAYER_TILE_SETS,
            tileMapWithTextureName = SuperimposedTilemaps.COMMON_TILE_MAP, textureId = "7")
        val DEFAULT_UNIT = TacMapUnitTemplate(8, _default_sit.copy(textureId = "6"), templateName = "Friendly",
                abilities = listOf(GuardAbility))
        val DEFAULT_ENEMY_UNIT = TacMapUnitTemplate(8, _demonImg.copy(textureId = "7"), templateName = "Enemy",
                abilities = listOf(StandardAbilities.SlimeStrike))
        val DEFAULT_ENEMY_UNIT_SPAWNER = TacMapUnitTemplate(0, _demonImg.copy(textureId = "8"), templateName = "EnemySpawner",
                abilities = listOf(StandardAbilities.SpawnUnit),
                enemyAiType = EnemyAiType.IMMOBILE_UNIT,
                walkableTerrainTypes = listOf())
        val MASTER_ORGAN = TacMapUnitTemplate(0,
                _demonImg.copy(textureId = "9"),
                templateName = "Master Organ",
                enemyAiType = EnemyAiType.IMMOBILE_UNIT,
                startingAttributes = listOf(LogicalCharacterAttribute.MASTER_ORGAN,
                        LogicalCharacterAttribute.EXPLODES_ON_DEATH))
        val SHIELDING_ORGAN = TacMapUnitTemplate(0,
                _demonImg.copy(textureId = "10"),
                templateName = "Shielding Organ",
                enemyAiType = EnemyAiType.IMMOBILE_UNIT,
                startingAttributes = listOf(LogicalCharacterAttribute.SHIELDS_ANOTHER_ORGAN,
                        LogicalCharacterAttribute.EXPLODES_ON_DEATH))
        val RANGED_ENEMY = TacMapUnitTemplate(3, _demonImg.copy(textureId = "7"), templateName = "Enemy",
                abilities = listOf(StandardAbilities.RangedAttack))
        val Dict = mapOf(TacMapUnitTemplateKeys.DEFAULT_UNIT to DEFAULT_UNIT,
                TacMapUnitTemplateKeys.DEFAULT_ENEMY_UNIT to DEFAULT_ENEMY_UNIT,
                TacMapUnitTemplateKeys.DEFAULT_ENEMY_UNIT_SPAWNER to DEFAULT_ENEMY_UNIT_SPAWNER)
    }
}

object TacMapUnitTemplateKeys{
    val DEFAULT_UNIT = "default_unit"
    val DEFAULT_ENEMY_UNIT = "default_enemy_unit"
    val DEFAULT_ENEMY_UNIT_SPAWNER = "default_enemy_unit_spawner"
}

public fun String.toTacMapUnitTemplate() : TacMapUnitTemplate?{
    return TacMapUnitTemplate.Dict[this]
}

@Autoinjectable
private class TemporaryTacMapUnitTemplateRegistration(){
    init{
        TacMapUnitTemplate.DEFAULT_ENEMY_UNIT.register()
    }
}