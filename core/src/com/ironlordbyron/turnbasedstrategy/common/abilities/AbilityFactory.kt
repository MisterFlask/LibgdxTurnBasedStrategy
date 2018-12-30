package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Transmutes the data of "logical abilities" into actual functions that have effects on the board.
 */
@Singleton
public class AbilityFactory @Inject constructor(val gameBoardOperator: GameBoardOperator,
                    val boardAlgorithms: TacticalMapAlgorithms,
                    val tacticalMapState: TacticalMapState,
                    val unitSpawner: EntitySpawner){
    fun acquireAbility(logicalAbilityAndEquipment: LogicalAbilityAndEquipment) : Ability {
        when(logicalAbilityAndEquipment.ability.abilityClass){
            AbilityClass.TARGETED_ABILITY -> return SimpleAttackAbility(logicalAbilityAndEquipment, tacticalMapState, boardAlgorithms, gameBoardOperator, boardAlgorithms,
                    unitSpawner)
        }
    }

}

enum class RequiredTargetType{
    ENEMY_ONLY, ALLY_ONLY, ANY, NO_CHARACTER_AT_LOCATION
}

interface Ability{
    val logicalAbilityAndEquipment: LogicalAbilityAndEquipment
    val tacticalMapState: TacticalMapState
    val tacticalMapAlgorithms: TacticalMapAlgorithms
    val logicalAbility:LogicalAbility
    get() = logicalAbilityAndEquipment.ability

    abstract fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?,
                               sourceCharacter: LogicalCharacter, equipment: LogicalEquipment?) : Boolean

    abstract fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?,
                                 sourceCharacter: LogicalCharacter, equipment: LogicalEquipment?)

    abstract fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter,  equipment: LogicalEquipment?, sourceSquare: TileLocation? = null) : Collection<TileLocation>

    // Like getValidAbilityTargetSquares, but takes into account allies vs enemies.
    // Note: SourceSquare is an optional parameter that represents where the logical character WOULD be using the abilityEquipmentPair from.
    fun getSquaresThatCanActuallyBeTargetedByAbility(sourceCharacter: LogicalCharacter, equipment: LogicalEquipment?, sourceSquare: TileLocation? = null): Collection<TileLocation>{
        val abilityTargetSquares = getValidAbilityTargetSquares(sourceCharacter, equipment, sourceSquare)
        val nearbyCharacters = tacticalMapState.listOfCharacters.filter{abilityTargetSquares.contains(it.tileLocation)}
        val possibilities = arrayListOf<LogicalCharacter>()
        for (target in nearbyCharacters){
            var opposingCharacters = target.playerAlly xor sourceCharacter.playerAlly
            if (logicalAbility.requiredTargetType == RequiredTargetType.ANY){
                possibilities.add(target)
            }
            else if (!opposingCharacters && logicalAbility.requiredTargetType == RequiredTargetType.ALLY_ONLY){
                possibilities.add(target)
            }
            else if (opposingCharacters && logicalAbility.requiredTargetType == RequiredTargetType.ENEMY_ONLY){
                possibilities.add(target)
            }
        }
        if (logicalAbility.requiredTargetType == RequiredTargetType.NO_CHARACTER_AT_LOCATION){
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
    public fun getWhereCharacterCanHitSomeoneWithThisAbility(logicalCharacter: LogicalCharacter, equipment: LogicalEquipment?) : ActionResult?{
        // for now, we'll just try to get one of the abilities in order.  Can do fancier stuff later.
        val abilities = logicalCharacter.abilities
        val moveableSquares = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(logicalCharacter)
        for (square in moveableSquares){
            for (abilityAndEquipment in abilities){
                val validTargetSquares = this.getSquaresThatCanActuallyBeTargetedByAbility(logicalCharacter, equipment, square)
                if (!validTargetSquares.isEmpty()){
                    return ActionResult(abilityAndEquipment, logicalCharacter, butFirstMoveHere = square, squaresTargetable =
                    validTargetSquares)
                }
            }
        }
        return null
    }

}


class SimpleAttackAbility(
        override val logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
        override val tacticalMapState: TacticalMapState,
        val boardAlgorithms: TacticalMapAlgorithms,
        val gameBoardOperator: GameBoardOperator,
        override val tacticalMapAlgorithms: TacticalMapAlgorithms,
        val unitSpawner: EntitySpawner) : Ability {
    override fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter,
                               equipment: LogicalEquipment?) : Boolean{
        return getValidAbilityTargetSquares(sourceCharacter, equipment).contains(location)
    }

    override fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter,
                                 equipment: LogicalEquipment?) {
        if (logicalAbility.damage != null){
            gameBoardOperator.damageCharacter(targetCharacter!!, !sourceCharacter.playerControlled, logicalAbility.damage!!)
        }
        // processing abilityEquipmentPair effects

        for (effect in logicalAbility.abilityEffects){
            when(effect){
                is LogicalAbilityEffect.SpawnsUnit -> unitSpawner.addCharacterToTile(effect.unitToBeSpawned.toTacMapUnitTemplate()!!, location!!,
                        sourceCharacter.playerControlled)
                is LogicalAbilityEffect.LightsTileOnFire -> unitSpawner.addFireToTile(location!!)
            }
        }
    }

    override fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter, equipment: LogicalEquipment?, sourceSquare: TileLocation?) : Collection<TileLocation>{
        return getTilesInRangeOfAbility(sourceCharacter, logicalAbility, sourceSquare)
    }
    private fun getTilesInRangeOfAbility(character: LogicalCharacter, ability: LogicalAbility, sourceSquare: TileLocation? = null): Collection<TileLocation> {
        val tiles = boardAlgorithms.getWalkableTileLocationsUpToNAway(ability.range, sourceSquare?:character.tileLocation, character,
                AlwaysValid())
        return tiles
    }
}