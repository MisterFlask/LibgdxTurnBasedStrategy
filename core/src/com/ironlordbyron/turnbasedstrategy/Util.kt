package com.ironlordbyron.turnbasedstrategy

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import java.util.*

private val boardState: TacticalMapState by lazy {
    GameModuleInjector.generateInstance(TacticalMapState::class.java)
}
public fun UUID.toCharacter() : LogicalCharacter {
    return boardState.getCharacterFromId(this)
}