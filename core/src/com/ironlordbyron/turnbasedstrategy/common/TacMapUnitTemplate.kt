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
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentSuperclass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.tacmapunits.TurnStartAction
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import java.util.*

data class Tags(val isSpawnableEnemy: Boolean = true)

data class EquipmentSlot(val allowedEquipment: Collection<EquipmentSuperclass>,
                         val name: String,
                         var currentEquipment: LogicalEquipment? = null){
    companion object{
        fun utilityOrVest(): EquipmentSlot {
            return EquipmentSlot(listOf(EquipmentSuperclass.UTILITY, EquipmentSuperclass.VEST), "Utility/Vest")
        }
        fun secondaryWeapon(): EquipmentSlot {
            return EquipmentSlot(listOf(EquipmentSuperclass.SECONDARY_WEAPON), "Secondary Weapon")
        }
        fun primaryWeapon(): EquipmentSlot {
            return EquipmentSlot(listOf(EquipmentSuperclass.PRIMARY_WEAPON), "Primary Weapon")
        }
    }

    fun isEquipmentAllowed(equipment: LogicalEquipment): Boolean {
        return allowedEquipment.any { it == equipment.equipmentClass.superclass }
    }
}

class TacMapUnitTemplate(val movesPerTurn: Int,
                         val tiledTexturePath: ProtoActor,
                         val templateName: String = "Peasant",
                         abilities: List<LogicalAbility> = listOf(),
                         var allowedEquipment: Collection<EquipmentClass> = listOf(EquipmentClass.MELEE_WEAPON_LARGE), //TODO
                         val walkableTerrainTypes : Collection<TerrainType> = listOf(TerrainType.GRASS, TerrainType.FOREST),
                         val enemyAiType: EnemyAiType = EnemyAiType.BASIC,
                         var startingAttributes: Collection<LogicalCharacterAttribute> = listOf(),
                         val unitId: UUID = UUID.randomUUID(),
                         var maxActionsLeft: Int = 2,
                         var maxHealth: Int = 3,
                         var healthLeft: Int = maxHealth,
                         attributes: ArrayList<LogicalCharacterAttribute> = ArrayList(startingAttributes),
                         val strength: Int = 0,
                         val dexterity: Int = 0,
                              // used in map generation
                         val difficulty: Int = 1,
                         val possibleRandomizedIntents: List<IntentType> = listOf(IntentType.ATTACK, IntentType.MOVE),
                         val templateId: String = templateName,
                         val metagoal: Metagoal = AttackMetaGoal(),
                         val turnStartAction: TurnStartAction? = null,
                         val tags: Tags = Tags(),
                         var block: Int = 0,
                         var equipmentSlots: List<EquipmentSlot> = listOf(EquipmentSlot.primaryWeapon(), EquipmentSlot.secondaryWeapon(),
                                 EquipmentSlot.utilityOrVest()),
                         val nonMinionEnemy: Boolean = true
) {

    val equipment: List<LogicalEquipment>
        get() = this.equipmentSlots.map{it.currentEquipment}.filterNotNull()
    private val stacksOfAttribute: HashMap<String, Int> = hashMapOf()
    private val _attributes = attributes
    private val _abilities = abilities.toList()

    public val abilities: List<LogicalAbilityAndEquipment>
    get(){
        val abilitiesSansEquipment = _abilities.toList().map{LogicalAbilityAndEquipment(it, null)}
        val abilitiesFromEquipment = ArrayList<LogicalAbilityAndEquipment>()
        for (equipmentSlot in equipmentSlots){
            if (equipmentSlot.currentEquipment != null){
                for (ability in equipmentSlot.currentEquipment!!.abilityEnabled){
                    abilitiesFromEquipment.add(LogicalAbilityAndEquipment(ability, equipmentSlot.currentEquipment))
                }
            }
        }
        return abilitiesFromEquipment + abilitiesSansEquipment
    }

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
    }
}
