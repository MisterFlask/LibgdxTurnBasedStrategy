package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.DamageType
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.TemporaryAnimationGenerator

enum class RequiredTargetType{
    ENEMY_ONLY, ALLY_ONLY, ANY, NO_CHARACTER_AT_LOCATION,

    DOOR
}

abstract class AbilityTargetingParameters{
    val tacticalMapState: TacticalMapState by lazy{
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val tacticalMapAlgorithms: TacticalMapAlgorithms by lazy{
        GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
    }

    abstract fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?,
                               sourceCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment) : Boolean

    abstract fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?,
                                 sourceCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment)

    abstract fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter,  logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation? = null) : Collection<TileLocation>

    // Like getValidAbilityTargetSquares, but takes into account allies vs enemies.
    // Note: SourceSquare is an optional parameter that represents where the logical character WOULD be using the abilityEquipmentPair from.
    fun getSquaresThatCanActuallyBeTargetedByAbility(sourceCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation? = null): Collection<TileLocation>{
        val abilityTargetSquares = getValidAbilityTargetSquares(sourceCharacter, logicalAbilityAndEquipment, sourceSquare)
        val nearbyCharacters = tacticalMapState.listOfCharacters.filter{abilityTargetSquares.contains(it.tileLocation)}
        val possibilities = arrayListOf<LogicalCharacter>()
        for (target in nearbyCharacters){
            var opposingCharacters = target.playerAlly xor sourceCharacter.playerAlly
            if (logicalAbilityAndEquipment.ability.requiredTargetType == RequiredTargetType.ANY){
                possibilities.add(target)
            }
            else if (!opposingCharacters && logicalAbilityAndEquipment.ability.requiredTargetType == RequiredTargetType.ALLY_ONLY){
                possibilities.add(target)
            }
            else if (opposingCharacters && logicalAbilityAndEquipment.ability.requiredTargetType == RequiredTargetType.ENEMY_ONLY){
                possibilities.add(target)
            }
        }
        if (logicalAbilityAndEquipment.ability.requiredTargetType == RequiredTargetType.DOOR){
            // we're just gonna say we can only hit doors if that's the target type.
            return abilityTargetSquares.filter { tacticalMapState.isDoorAt(it) }
        }

        if (logicalAbilityAndEquipment.ability.requiredTargetType == RequiredTargetType.NO_CHARACTER_AT_LOCATION){
            val nearbyCharacterLocations = nearbyCharacters.map{char -> char.tileLocation}
            return abilityTargetSquares.filter{!nearbyCharacterLocations.contains(it)}
        }
        return possibilities.map{it.tileLocation}
    }



    /**
     * Returns all places that the character can move to which satisfies ALL of the following criteria:
     * 1)  The character can move to this location THIS turn, thus satisfying movement ranges.
     * 2)  The character can hit someone with an abilityEquipmentPair that character possesses.
     */
    public fun getWhereCharacterCanHitSomeoneWithThisAbility(logicalCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment) : ActionResult?{
        // for now, we'll just try to get one of the abilities in order.  Can do fancier stuff later.
        val abilities = logicalCharacter.abilities
        val moveableSquares = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(logicalCharacter)
        for (square in moveableSquares){
            for (abilityAndEquipment in abilities){
                val validTargetSquares = this.getSquaresThatCanActuallyBeTargetedByAbility(logicalCharacter, logicalAbilityAndEquipment, square)
                if (!validTargetSquares.isEmpty()){
                    return ActionResult(abilityAndEquipment, logicalCharacter, butFirstMoveHere = square, squaresTargetable =
                    validTargetSquares)
                }
            }
        }
        return null
    }

}


class SimpleAttackAbility() : AbilityTargetingParameters() {

    val boardAlgorithms: TacticalMapAlgorithms by lazy{
        GameModuleInjector.generateInstance(TacticalMapAlgorithms::class.java)
    }
    val damageOperator: DamageOperator by lazy{
        GameModuleInjector.generateInstance(DamageOperator::class.java)
    }
    val unitSpawner: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val animationActionQueueProvider: AnimationActionQueueProvider by lazy{
        GameModuleInjector.generateInstance(AnimationActionQueueProvider::class.java)
    }
    val temporaryAnimationGenerator: TemporaryAnimationGenerator by  lazy{
        GameModuleInjector.generateInstance(TemporaryAnimationGenerator::class.java)
    }
    val logicHooks: LogicHooks by lazy{
        GameModuleInjector.generateInstance(LogicHooks::class.java)
    }

    override fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter,
                               logicalAbilityAndEquipment: LogicalAbilityAndEquipment) : Boolean{
        return getValidAbilityTargetSquares(sourceCharacter, logicalAbilityAndEquipment).contains(location)
    }

    override fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter,
                                 logicalAbilityAndEquipment: LogicalAbilityAndEquipment) {

        if (location == null){
            throw NotImplementedError("Havne't yet implemented non-location abilities")
        }

        val locationsAffected = logicalAbilityAndEquipment.ability.areaOfEffect.getTilesAffected(location, sourceCharacter, logicalAbilityAndEquipment)

        for (locationInAoe in locationsAffected){
            runAbilityOnLocation(logicalAbilityAndEquipment, locationInAoe, sourceCharacter, targetCharacter)
        }

        if (sourceCharacter.playerControlled){
            animationActionQueueProvider.runThroughActionQueue(finalAction = {})
            animationActionQueueProvider.clearQueue()
        }
    }

    private fun runAbilityOnLocation(logicalAbilityAndEquipment: LogicalAbilityAndEquipment, targetedLocation: TileLocation, sourceCharacter: LogicalCharacter, targetCharacter: LogicalCharacter?) {
        val ability = logicalAbilityAndEquipment.ability
        val landingActor = ability.landingActor
        // if there's a projectile, do it
        if (ability.projectileActor != null) {
            val projectile = unitSpawner.animateProjectileForLogicalAbility(logicalAbilityAndEquipment, sourceCharacter.tileLocation, targetedLocation)
            if (projectile != null) {
                animationActionQueueProvider.addAction(projectile)
            }
        }
        // for each tile in the area of effect
        val areaOfEffect = logicalAbilityAndEquipment.ability.areaOfEffect.getTilesAffected(targetedLocation,
                sourceCharacter, logicalAbilityAndEquipment)
        for (affectedTile in areaOfEffect){
            // if there's an effect, do it
            if (ability.landingActor != null) {
                val lander = temporaryAnimationGenerator.getTemporaryAnimationActorActionPair(affectedTile, landingActor!!)
                animationActionQueueProvider.addAction(lander)
            }

            for (effect in logicalAbilityAndEquipment.ability.abilityEffects) {
                effect.runAction(sourceCharacter, affectedTile)
            }
            val damage = logicalAbilityAndEquipment.ability.damage
            // if there's a damage effect, do that (probably just the number-rising thing)
            if (damage != null) {
                val logicHooksAttemptToDamageResult = logicHooks.attemptToDamage(DamageAttemptInput(
                        sourceCharacter,
                        targetCharacter!!,
                        logicalAbilityAndEquipment,
                        damage,
                        DamageType.FIRE //todo
                ))
                // so, the GBO shouldn't be responsible for handing damage animations, because those will vary based on attack.
                damageOperator.damageCharacter(logicHooksAttemptToDamageResult.targetCharacter,
                        logicHooksAttemptToDamageResult.damage, logicalAbilityAndEquipment,
                        logicHooksAttemptToDamageResult.sourceCharacter)
            }
        }
    }

    override fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?) : Collection<TileLocation>{
        return getTilesInRangeOfAbility(sourceCharacter, logicalAbilityAndEquipment, sourceSquare)
    }
    private fun getTilesInRangeOfAbility(character: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation? = null): Collection<TileLocation> {
        // BUG:  this SHOULD be referencing sourceSquare.  NOT character.
        val tiles = logicalAbilityAndEquipment.ability.rangeStyle.getTargetableTiles(character, logicalAbilityAndEquipment, sourceSquare)
        return tiles
    }
}