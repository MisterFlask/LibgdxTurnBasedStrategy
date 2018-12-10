package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapAlgorithms
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.AbilityClass
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import javax.inject.Inject
import javax.inject.Singleton

class AbilityEffectFactory {

}

/**
 * Transmutes the data of "logical abilities" into actual functions that have effects on the board.
 */
@Singleton
public class AbilityFactory @Inject constructor(val gameBoardOperator: GameBoardOperator,
                    val boardAlgorithms: TacticalMapAlgorithms){
    fun acquireAbility(logicalAbility: LogicalAbility) : Ability{
        when(logicalAbility.abilityClass){
            AbilityClass.TARGETED_ABILITY -> return SimpleAttackAbility(logicalAbility, boardAlgorithms, gameBoardOperator)
        }
    }

}

interface Ability{
    abstract fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?,
                               sourceCharacter: LogicalCharacter) : Boolean

    abstract fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?,
                                 sourceCharacter: LogicalCharacter)

    abstract fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter) : Collection<TileLocation>
}


class SimpleAttackAbility(
                          val logicalAbility: LogicalAbility,
                          val boardAlgorithms: TacticalMapAlgorithms,
                          val gameBoardOperator: GameBoardOperator) : Ability{
    override fun isValidTarget(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter) : Boolean{
        return getValidAbilityTargetSquares(sourceCharacter).contains(location)
    }

    override fun activateAbility(location: TileLocation?, targetCharacter: LogicalCharacter?, sourceCharacter: LogicalCharacter) {
        gameBoardOperator.damageCharacter(targetCharacter)
    }

    override fun getValidAbilityTargetSquares(sourceCharacter: LogicalCharacter) : Collection<TileLocation>{
        return boardAlgorithms.getTilesInRangeOfAbility(sourceCharacter, logicalAbility)
    }

}