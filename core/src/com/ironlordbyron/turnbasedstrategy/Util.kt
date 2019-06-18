package com.ironlordbyron.turnbasedstrategy

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import java.util.*
import kotlin.reflect.KClass

private val boardState: TacticalMapState by lazy {
    GameModuleInjector.generateInstance(TacticalMapState::class.java)
}
public fun UUID.toCharacter() : LogicalCharacter {
    return boardState.getCharacterFromId(this)
}
public fun LogicalCharacter.isAdjacentTo(other: LogicalCharacter) : Boolean{
    val distance = Math.abs(other.tileLocation.x - this.tileLocation.x) +
            Math.abs(other.tileLocation.y - this.tileLocation.y)
    return distance == 1
}

public fun <T: LogicalCharacterAttribute> LogicalCharacter.getAttribute(clazz: KClass<T>) : T{
    return this.attributes.find { clazz.isInstance(it)} as T
}