package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.*
import javax.inject.Inject
import javax.inject.Singleton

class AbilityEffectFactory {

}

/**
 * Transmutes the data of "logical abilities" into actual functions that have effects on the board.
 */
@Singleton
public class AbilityFactory @Inject constructor(val gameBoardOperator: GameBoardOperator,
                    val boardAlgorithms: TacticalMapAlgorithms,
                    val tacticalMapState: TacticalMapState){
    fun acquireAbility(logicalAbility: LogicalAbility) : Ability {
        when(logicalAbility.abilityClass){
            AbilityClass.TARGETED_ABILITY -> return SimpleAttackAbility(logicalAbility, tacticalMapState, boardAlgorithms, gameBoardOperator, boardAlgorithms)
        }
    }

}

enum class RequiredTargetType{
    ENEMY_ONLY, ALLY_ONLY, ANY
}

interface Ability{
    val logicalAbility: LogicalAbility
    val tacticalMapState: TacticalMapState
    val tacticalMapAlgorithms: TacticalMapAlgorithms

    abstract fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?,
                               sourceCharacter: LogicalCharacter) : Boolean

    abstract fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?,
                                 sourceCharacter: LogicalCharacter)

    abstract fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter, sourceSquare: TileLocation? = null) : Collection<TileLocation>

    // Like getValidAbilityTargetSquares, but takes into account allies vs enemies.
    // Note: SourceSquare is an optional parameter that represents where the logical character WOULD be using the ability from.
    fun getSquaresThatCanActuallyBeTargetedByAbility(sourceCharacter: LogicalCharacter, sourceSquare: TileLocation?): Collection<TileLocation>{
        val abilityTargetSquares = getValidAbilityTargetSquares(sourceCharacter, sourceSquare)
        val nearbyCharacters = tacticalMapState.listOfCharacters.filter{abilityTargetSquares.contains(it.tileLocation)}
        val possibilities = arrayListOf<LogicalCharacter>()
        for (target in nearbyCharacters){
            if (logicalAbility.requiredTargetType == RequiredTargetType.ANY){
                possibilities.add(target)
            }
            else if (sourceCharacter.playerAlly && logicalAbility.requiredTargetType != RequiredTargetType.ENEMY_ONLY){
                possibilities.add(target)
            }
            else if (!sourceCharacter.playerAlly && logicalAbility.requiredTargetType != RequiredTargetType.ALLY_ONLY){
                possibilities.add(target)
            }
        }
        return possibilities.map{it.tileLocation}

    }



    /**
     * Returns all places that the character can move to which satisfies ALL of the following criteria:
     * 1)  The character can move to this location THIS turn, thus satisfying movement ranges.
     * 2)  The character can hit someone with an ability that character possesses.
     */
    public fun getWhereCharacterCanHitSomeoneWithThisAbility(logicalCharacter: LogicalCharacter) : ActionResult?{
        // for now, we'll just try to get one of the abilities in order.  Can do fancier stuff later.
        val abilities = logicalCharacter.abilities
        val moveableSquares = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(logicalCharacter)
        for (square in moveableSquares){
            for (ability in abilities){
                val validTargetSquares = this.getSquaresThatCanActuallyBeTargetedByAbility(logicalCharacter, square)
                if (!validTargetSquares.isEmpty()){
                    return ActionResult(ability, logicalCharacter, butFirstMoveHere = square, squaresTargetable =
                    validTargetSquares)
                }
            }
        }
        return null
    }

}


class SimpleAttackAbility(
        override val logicalAbility: LogicalAbility,
        override val tacticalMapState: TacticalMapState,
        val boardAlgorithms: TacticalMapAlgorithms,
        val gameBoardOperator: GameBoardOperator, override val tacticalMapAlgorithms: TacticalMapAlgorithms) : Ability {
    override fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter) : Boolean{
        return getValidAbilityTargetSquares(sourceCharacter).contains(location)
    }

    override fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter) {
        gameBoardOperator.damageCharacter(targetCharacter!!)
    }

    override fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter, sourceSquare: TileLocation?) : Collection<TileLocation>{
        return getTilesInRangeOfAbility(sourceCharacter, logicalAbility, sourceSquare)
    }
    private fun getTilesInRangeOfAbility(character: LogicalCharacter, ability: LogicalAbility, sourceSquare: TileLocation? = null): Collection<TileLocation> {
        val tiles = boardAlgorithms.getWalkableTileLocationsUpToNAway(ability.range, sourceSquare?:character.tileLocation, character,
                AlwaysValid())
        return tiles
    }
}