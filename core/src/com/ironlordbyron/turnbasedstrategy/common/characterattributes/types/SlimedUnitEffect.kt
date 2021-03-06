package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Autoinjectable
class SlimedUnitFunctionalEffect @Inject constructor() : FunctionalAttributeEffect() {
    override fun getMovementModifier(params: FunctionalEffectParameters): Int {
        return -1 * params.stacks
    }
}