package com.ironlordbyron.turnbasedstrategy.common


data class TileLocation(val x: Int, val y: Int){

    public override fun toString(): String{
        return "[$x,$y]"
    }
}
