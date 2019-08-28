package com.ironlordbyron.turnbasedstrategy.common.characterattributes

import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.getAttribute
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tacmapunits.tacMapState
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.PainterlyIcons
import java.util.*

public class SleepingGuardian(val guardingCharacter: UUID): LogicalCharacterAttribute("Sleeping Guardian",
        PainterlyIcons.PROTECT_SKY.toProtoActor(1),
        description = {"Awakens when its organ is attacked."},
        tacticalMapProtoActor = DataDrivenOnePageAnimation.SNOOZE_ACTOR,
        id = "SLEEPING_GUARDIAN"
        ) {
    override val stopsUnitFromActing: Boolean
        get() = true
}

val attributeOperator by LazyInject(AttributeActionManager::class.java)

public class AdrenalGlands(): LogicalCharacterAttribute("Adrenal Glands",
        PainterlyIcons.FOG_SKY.toProtoActor(1),
        description = {"Awakens all Sleeping Guardians when attacked."}){
    override fun onBeingStruck(params: FunctionalEffectParameters) {
        val id = params.thisCharacter.tacMapUnit.unitId

        val relevantCharacters = tacMapState.listOfCharacters.filter{it.hasAttributeWithId("SLEEPING_GUARDIAN")}
                .filter{it.getAttribute(SleepingGuardian::class).guardingCharacter == id}
        for (char in relevantCharacters){
            attributeOperator.unapplyAttribute(char, char.getAttribute(SleepingGuardian::class))
        }
    }
}