package com.ironlordbyron.turnbasedstrategy.common.characterattributes.types

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.entrypoints.LogicalEffect
import javax.inject.Inject
import javax.inject.Singleton


@Autoinjectable
@Singleton
public class OnFireFunctionalEffect @Inject constructor (val damageOperator: DamageOperator,
                                                         override val eventNotifier: EventNotifier) : FunctionalUnitEffect<OnFireLogicalEffect> {
    override val id: String = "ON_FIRE"
    override val clazz: Class<OnFireLogicalEffect> = OnFireLogicalEffect::class.java

    override fun onTurnStart(logicalAttr: OnFireLogicalEffect, thisCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute) {
        damageOperator.damageCharacter(thisCharacter, damageAmount = logicalAttr.damagePerTurn, abilityAndEquipment = null, sourceCharacter = null)
    }

    companion object {
        // for use as key in map
        fun toEntry(onFireLogicalAttribute: OnFireLogicalEffect) : Pair<String, Any>{
            return "ON_FIRE" to onFireLogicalAttribute
        }

        val ON_FIRE_ATTRIBUTE  = LogicalCharacterAttribute("On Fire",
                LogicalCharacterAttribute._demonImg.copy(textureId = "6"),
                statusEffect = true,
                customEffects = mapOf(OnFireLogicalEffect(1).toPair()),
                description = {"This unit is on fire and will take one damage per turn until it's put out."})
    }
}


public data class OnFireLogicalEffect(val damagePerTurn: Int) : LogicalEffect {
    override val id = "ON_FIRE"
}