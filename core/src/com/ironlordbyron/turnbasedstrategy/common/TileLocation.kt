package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector


data class TileLocation(val x: Int, val y: Int){

    public override fun toString(): String{
        return "[$x,$y]"
    }
}

private val gameState: TacticalMapState by lazy {
    GameModuleInjector.generateInstance(TacticalMapState::class.java)
}
public fun TileLocation.getCharacter() : LogicalCharacter? {
    return gameState.characterAt(this)
}
