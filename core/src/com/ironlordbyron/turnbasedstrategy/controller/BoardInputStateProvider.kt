package com.ironlordbyron.turnbasedstrategy.controller

import com.google.inject.ImplementedBy
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState

@ImplementedBy(TacticalMapController::class)
interface BoardInputStateProvider {
    val boardInputState : BoardInputState
}
