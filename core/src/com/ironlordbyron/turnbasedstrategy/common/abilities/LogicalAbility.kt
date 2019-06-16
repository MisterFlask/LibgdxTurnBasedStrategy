package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.ai.IntentType
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import sun.rmi.runtime.Log

public class LogicalAbility(val name: String,
                            val speed: AbilitySpeed,
                            val range: Int,
                            val attackSprite: ProtoActor? = null,
                            val missionLimit: Int? = null,
                            val damage: Int? = null,
                            val description: String? = null,
                            val abilityClass: AbilityClass,
                            val allowsTargetingSelf: Boolean = false,
                            val requiredTargetType: RequiredTargetType = RequiredTargetType.ANY,
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
                            val intentType: IntentType = IntentType.ATTACK,
                            val requiresTarget: Boolean = true,
                            val abilityTargetingParameters: AbilityTargetingParameters = SimpleAttackAbility()){

}

interface RangeStyle{
    fun getTargetableTiles(characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?): Collection<TileLocation>

    public class Simple(val range: Int) : RangeStyle{
        override fun getTargetableTiles(characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?): Collection<TileLocation> {
            val algorithms = GameModuleInjector.moduleInjector.getInstance(TacticalMapAlgorithms::class.java)
            sourceSquare!!
            return algorithms.getTileLocationsUpToNAway(range, sourceSquare, characterUsing)
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
            val algorithms = GameModuleInjector.moduleInjector.getInstance(TacticalMapAlgorithms::class.java)
            return algorithms.getTileLocationsUpToNAway(radius, tileLocationTargeted, characterUsing)
        }
    }

}

interface LogicalAbilityEffect {
    fun runAction(characterUsing: LogicalCharacter,
                  tileLocationTargeted: TileLocation)

    public data class SpawnsUnit(val unitToBeSpawned: String): LogicalAbilityEffect{
        val unitSpawner = GameModuleInjector.generateInstance(ActionManager::class.java)
        val animationActionQueueProvider = GameModuleInjector.generateInstance(AnimationActionQueueProvider::class.java)

        override fun runAction(characterUsing: LogicalCharacter,
                               tileLocationTargeted: TileLocation) {
            unitSpawner.addCharacterToTileFromTemplate(unitToBeSpawned.toTacMapUnitTemplate()!!, tileLocationTargeted!!,
                    characterUsing.playerControlled)        }
    }


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
            animationActionQueueProvider.addAction(unitSpawner.openDoorAction(tileLocationTargeted))
        }
    }

    class CreatePortal : LogicalAbilityEffect {
        val unitSpawner = GameModuleInjector.generateInstance(ActionManager::class.java)
        val animationActionQueueProvider = GameModuleInjector.generateInstance(AnimationActionQueueProvider::class.java)

        override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
            // unitSpawner.spawnEntityAtTileInSequence(tileLocationTargeted))
            // TODO
        }

    }
}

@Deprecated("Deprecated feature; we're just using requiresTarget instead.")
public enum class AbilityClass {
    TARGETED_ATTACK_ABILITY
}
