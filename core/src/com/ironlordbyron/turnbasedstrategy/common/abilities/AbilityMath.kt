package com.ironlordbyron.turnbasedstrategy.common.abilities

import com.ironlordbyron.turnbasedstrategy.common.LogicalAbilityAndEquipment
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import java.lang.IllegalStateException
import java.lang.Math.sqrt
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

data class NormalizedTileLocationVector(val x: Int, val y: Int) {

    init{
        if (x > 1 || x < -1){
            throw IllegalStateException("Non-normalized location vector: $x, $y")
        }
        if (y > 1 || y < -1){
            throw IllegalStateException("Non-normalized location vector: $x, $y")
        }
        if (abs(x) + abs(y) != 1){
            throw IllegalStateException("Non-normalized location vector: $x, $y")
        }
    }

    fun left(): NormalizedTileLocationVector {
        return NormalizedTileLocationVector(this.y * -1, this.x)
    }
    fun right() : NormalizedTileLocationVector{
        return NormalizedTileLocationVector(this.y, this.x * -1)
    }


    companion object {
        fun fromRelativeLocations(origin: TileLocation, destination: TileLocation): NormalizedTileLocationVector {
            val dx = destination.x - origin.x
            val dy = destination.y - origin.y
            val len = sqrt(dx.toDouble().pow(2) + dy.toDouble().pow(2))
            val normDx = dx / len
            val normDy = dy / len
            val roundedX = normDx.roundToInt()
            val roundedY = normDy.roundToInt()
            if (roundedX.absoluteValue + roundedY.absoluteValue != 1) {
                return NormalizedTileLocationVector(1, 0)
                // safeguard in case of rounding issues resulting in two 0s or 1s
            } else {
                return NormalizedTileLocationVector(roundedX, roundedY)
            }
        }
    }
}

data class AbilityTileCollector(val vector: NormalizedTileLocationVector, var currentLocationPointer: TileLocation){

    val tilesSoFar: HashSet<TileLocation> = HashSet<TileLocation>()

    fun forward(distance: Int = 1): AbilityTileCollector{
        currentLocationPointer = currentLocationPointer.forward(vector, distance)
        return this
    }

    fun addHere(): AbilityTileCollector{
        tilesSoFar.add(currentLocationPointer)
        return this
    }

    fun addWiden(distance: Int = 1){
        val left = vector.left()
        val right = vector.right()
        tilesSoFar.add(currentLocationPointer)
        for (i in 0 .. distance){
            tilesSoFar.add(currentLocationPointer.forward(left, i))
            tilesSoFar.add(currentLocationPointer.forward(right, i))
        }
    }

    fun collect(): HashSet<TileLocation> {
        return tilesSoFar
    }
}

fun TileLocation.forward(forwardVector: NormalizedTileLocationVector, distance: Int): TileLocation {
    return TileLocation(this.x + forwardVector.x * distance, this.y + forwardVector.y * distance)
}