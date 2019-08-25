package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.ai.EnemyAiType
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.ai.goals.AttackMetaGoal
import com.ironlordbyron.turnbasedstrategy.ai.goals.Metagoal
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.abilities.StandardAbilities
import com.ironlordbyron.turnbasedstrategy.common.abilities.specific.GuardAbility
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tacmapunits.TurnStartAction
import com.ironlordbyron.turnbasedstrategy.tacmapunits.WeakMinionSpawner
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.register
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import java.util.*

data class Tags(val isSpawnableEnemy: Boolean = true)

class TacMapUnitTemplate(val movesPerTurn: Int,
                              val tiledTexturePath: ProtoActor,
                              val templateName: String = "Peasant",
                              var abilities: List<LogicalAbility> = listOf(),
                              var allowedEquipment: Collection<EquipmentClass> = listOf(EquipmentClass.MELEE_WEAPON_LARGE), //TODO
                              val walkableTerrainTypes : Collection<TerrainType> = listOf(TerrainType.GRASS, TerrainType.FOREST),
                              val enemyAiType: EnemyAiType = EnemyAiType.BASIC,
                              var startingAttributes: Collection<LogicalCharacterAttribute> = listOf(),
                              val uuid: UUID = UUID.randomUUID(),
                              var maxActionsLeft: Int = 2,
                              var maxHealth: Int = 3,
                              var healthLeft: Int = maxHealth,
                              var equipment: ArrayList<LogicalEquipment> = ArrayList(),
                              attributes: ArrayList<LogicalCharacterAttribute> = ArrayList(startingAttributes),
                              val strength: Int = 0,
                              val dexterity: Int = 0,
                              // used in map generation
                              val difficulty: Int = 1,
                              val possibleRandomizedIntents: List<IntentType> = listOf(IntentType.ATTACK, IntentType.MOVE),
                              val templateId: String = templateName,
                              val metagoal: Metagoal = AttackMetaGoal(),
                              val turnStartAction: TurnStartAction? = null,
                              val tags: Tags = Tags()
) {

    private val stacksOfAttribute: HashMap<String, Int> = hashMapOf()
    private val _attributes = attributes

    init{
        for (attribute in attributes){
            stacksOfAttribute[attribute.id] = 1
        }
    }

    fun getAttributes() : Collection<LogicalCharacter.StacksOfAttribute> {
        val ret = ArrayList<LogicalCharacter.StacksOfAttribute>()
        for (attribute in _attributes){
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
            _attributes.add(logicalAttribute)
            stacksOfAttribute[logicalAttribute.id] = stacks
        }
        if (stacksOfAttribute[logicalAttribute.id] == 0){
            this._attributes.remove(logicalAttribute)
            stacksOfAttribute.remove(logicalAttribute.id)
        }
    }
    fun removeAttributeById(id: String){
        if (stacksOfAttribute.containsKey(id)){
            stacksOfAttribute.remove(id)
        }
        _attributes.removeIf{it.id == id}
    }
    fun removeAttribute(logicalAttribute: LogicalCharacterAttribute){
        if (stacksOfAttribute.containsKey(logicalAttribute.id)){
            stacksOfAttribute.remove(logicalAttribute.id)
        }
        _attributes.remove(logicalAttribute)
    }

    // defensive copying
    init{
        equipment = ArrayList(equipment)
        abilities = ArrayList(abilities)
        allowedEquipment = ArrayList(allowedEquipment)
        startingAttributes = ArrayList(startingAttributes)
    }
    companion object TacMapUnit {
        val _demonImg = SuperimposedTilemaps(tileSetNames = listOf("Demon0","Demon1"), textureId = "2")
        internal val _default_sit = SuperimposedTilemaps(tileSetNames = SuperimposedTilemaps.PLAYER_TILE_SETS,
            tileMapWithTextureName = SuperimposedTilemaps.COMMON_TILE_MAP, textureId = "7")
        val DEFAULT_UNIT = TacMapUnitTemplate(8, _default_sit.copy(textureId = "6"), templateName = "Friendly",
                abilities = listOf(GuardAbility))
        val DEFAULT_ENEMY_UNIT = TacMapUnitTemplate(8, _demonImg.copy(textureId = "7"), templateName = "Enemy",
                abilities = listOf(StandardAbilities.SlimeStrike))
        val MASTER_ORGAN = TacMapUnitTemplate(0,
                _demonImg.copy(textureId = "9"),
                templateName = "Master Organ",
                enemyAiType = EnemyAiType.IMMOBILE_UNIT,
                startingAttributes = listOf(LogicalCharacterAttribute.MASTER_ORGAN,
                        LogicalCharacterAttribute.EXPLODES_ON_DEATH))
        val RANGED_ENEMY = TacMapUnitTemplate(3, _demonImg.copy(textureId = "7"), templateName = "Enemy",
                abilities = listOf(StandardAbilities.RangedAttack))
        val Dict = mapOf(TacMapUnitTemplateKeys.DEFAULT_UNIT to DEFAULT_UNIT,
                TacMapUnitTemplateKeys.DEFAULT_ENEMY_UNIT to DEFAULT_ENEMY_UNIT,
                TacMapUnitTemplateKeys.DEFAULT_ENEMY_UNIT_SPAWNER to WeakMinionSpawner())
    }
}

object TacMapUnitTemplateKeys{
    val DEFAULT_UNIT = "default_unit"
    val DEFAULT_ENEMY_UNIT = "default_enemy_unit"
    val DEFAULT_ENEMY_UNIT_SPAWNER = "default_enemy_unit_spawner"
}

val unitTemplateRegistrar: UnitTemplateRegistrar by LazyInject(UnitTemplateRegistrar::class.java)
public fun String.toTacMapUnitTemplate() : TacMapUnitTemplate{
    return unitTemplateRegistrar.getTacMapUnitById(this)?:throw java.lang.IllegalArgumentException("Could not find tac map unit template ID for $this")
}


@Autoinjectable
private class TemporaryTacMapUnitTemplateRegistration(){
    init{
        TacMapUnitTemplate.DEFAULT_ENEMY_UNIT.register()
    }
}

val tacMapUnitTemplateRegistrar: UnitTemplateRegistrar by LazyInject(UnitTemplateRegistrar::class.java)
private fun TacMapUnitTemplate.TacMapUnit.fromId(id: String): TacMapUnitTemplate {
    return tacMapUnitTemplateRegistrar.getTacMapUnitById(id)?:throw IllegalArgumentException("$id is not a valid tac map unit template ID")
}
