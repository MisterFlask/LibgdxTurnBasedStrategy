package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.ai.AiPlannedAction
import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tacmapunits.classes.nonclassabilities.NullTileFilter
import com.ironlordbyron.turnbasedstrategy.tacmapunits.classes.nonclassabilities.TileFilter
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor

public class LogicalAbility(val name: String,
                            val speed: AbilitySpeed,
                            @Deprecated("use rangeStyle instead")
                                 val range: Int,
                            val attackSprite: ProtoActor? = null,
                            val missionLimit: Int? = null,
                            @Deprecated("use damageStyle instead")
                                 val damage: Int? = null,
                            val description: String? = null,
                            val abilityClass: AbilityClass,
                            val allowsTargetingSelf: Boolean = false,
                            val requiredTargetType: RequiredTargetType = RequiredTargetType.ANY_CHARACTER,
                            val abilityEffects: Collection<LogicalAbilityEffect> = listOf(),
                            // if this is non-null, there will be a projectile animation.
                            val projectileActor: ProtoActor?,
                            // this is the actor that is spawned when the projectile lands. (Like: a fireball projectile
                            // could result in a langingActor being an explosion.
                            // a projectileActor is NOT required for this to function.
                            val landingActor: ProtoActor?,
                            // this is specifically for contextual abilities, like opening doors.
                            val requirement: ContextualAbilityRequirement? = null,
                            val inflictsStatusAffect: Collection<LogicalCharacterAttribute> = listOf(),
                            val areaOfEffect: AreaOfEffect = AreaOfEffect.One(),
                            val cooldownTurns: Int? = null,
                            val rangeStyle: RangeStyle = RangeStyle.Simple(range),
                            val damageStyle: DamageStyle = SimpleDamageStyle(damage?:0),
                            val intentType: IntentType = IntentType.ATTACK,
                            val requiresTarget: Boolean = true,
                            val abilityTargetingParameters: AbilityTargetingParameters = SimpleAttackAbility(),
                            val customAbilityAi: CustomAbilityAi? = null,
                            val mpCost: Int = 0,
                            val id: String = name,
                            val abilityUsageTileFilter : TileFilter = NullTileFilter()){

}

interface CustomAbilityAi {
    fun abilityDesireabilityToUse(sourceCharacter: LogicalCharacter) : Int
    fun getActionsForAbilityUse(sourceCharacter: LogicalCharacter) : List<AiPlannedAction>
    fun canUseAbility(sourceCharacter: LogicalCharacter): Boolean
    fun getMovementGoal(sourceCharacter: LogicalCharacter) : TileLocation
}

interface DamageStyle{
    fun getDamageAmount(characterUsing: LogicalCharacter,
                        logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                        targetCharacter: LogicalCharacter): Int
}

class SimpleDamageStyle(val amount: Int): DamageStyle{
    override fun getDamageAmount(characterUsing: LogicalCharacter,
                                 logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                                 targetCharacter: LogicalCharacter): Int {
        return amount
    }

}

interface RangeStyle{
    fun getTargetableTiles(characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?): Collection<TileLocation>

    public class Linear(val maxRange: Int?) : RangeStyle{
        val tacticalMapState: TacticalMapState by lazy{
            GameModuleInjector.generateInstance(TacticalMapState::class.java)
        }
        val tileMapProvider: TileMapProvider by lazy{
            GameModuleInjector.generateInstance(TileMapProvider::class.java)
        }
        override fun getTargetableTiles(characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?): Collection<TileLocation> {
            val tiles = ArrayList<TileLocation>()
            val y1 = characterUsing.tileLocation.y
            for (x1 in 0 .. tileMapProvider.getWidth()){
                val location = TileLocation(x1,y1)
                if (maxRange == null){
                    tiles.add(location)
                } else{
                    if (maxRange < location.distanceTo(characterUsing.tileLocation)){
                        tiles.add(location)
                    }
                }
            }
            val x2 = characterUsing.tileLocation.x
            for (y2 in 0 .. tileMapProvider.getHeight()){
                val location = TileLocation(x2,y2)
                if (maxRange == null){
                    tiles.add(location)
                } else{
                    if (maxRange < location.distanceTo(characterUsing.tileLocation)){
                        tiles.add(location)
                    }
                }
            }
            return tiles
        }
    }

    public class Simple(val maxRange: Int, val minRange: Int = 1) : RangeStyle{
        override fun getTargetableTiles(characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?): Collection<TileLocation> {
            val algorithms = GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
            sourceSquare!!
            return algorithms.getTileLocationsUpToNAway(maxRange, sourceSquare, characterUsing)
                    .filter{it.distanceTo(characterUsing.tileLocation) >= minRange}
        }
    }
}

interface AreaOfEffect{

    fun getTilesAffected(tileLocationTargeted: TileLocation, characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment) : Collection<TileLocation>

    // Affects only the tile targeted.
    public class One() : AreaOfEffect{
        override fun getTilesAffected(tileLocationTargeted: TileLocation, characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment): Collection<TileLocation> {
            return setOf(tileLocationTargeted);
        }
    }

    // Affects tiles in a radius around the target.
    public class Aoe(val radius : Int) : AreaOfEffect{
        override fun getTilesAffected(tileLocationTargeted: TileLocation, characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment): Collection<TileLocation> {
            val algorithms = GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
            return algorithms.getTileLocationsUpToNAway(radius, tileLocationTargeted, characterUsing)
        }
    }

}

interface LogicalAbilityEffect {
    fun runAction(characterUsing: LogicalCharacter,
                  tileLocationTargeted: TileLocation)



    public class LightsTileOnFire: LogicalAbilityEffect{
        val unitSpawner = GameModuleInjector.generateInstance(ActionManager::class.java)
        val animationActionQueueProvider = GameModuleInjector.generateInstance(AnimationActionQueueProvider::class.java)

        override fun runAction(characterUsing: LogicalCharacter,
                               tileLocationTargeted: TileLocation) {
            animationActionQueueProvider.addAction(unitSpawner.generateLightTileOnFireAction(tileLocationTargeted))
        }
    }

    class OpensDoor: LogicalAbilityEffect{
        val unitSpawner = GameModuleInjector.generateInstance(ActionManager::class.java)
        val animationActionQueueProvider = GameModuleInjector.generateInstance(AnimationActionQueueProvider::class.java)

        override fun runAction(characterUsing: LogicalCharacter,
                               tileLocationTargeted: TileLocation) {
            unitSpawner.openDoorAction(tileLocationTargeted)
        }
    }

}


@Deprecated("Deprecated feature; we're just using requiresTarget instead.")
public enum class AbilityClass {
    TARGETED_ATTACK_ABILITY
}
