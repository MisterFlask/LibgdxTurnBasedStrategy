package com.ironlordbyron.turnbasedstrategy.tacmapunits.equipment

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeActionManager
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps

public fun Shotgun() : LogicalEquipment {
    // requires reload after every shot
    return LogicalEquipment("Shotgun",
            EquipmentClass.SHOTGUN,
            listOf(fireShotgun()))

}

fun fireShotgun(): LogicalAbility {
    return LogicalAbility("Fire Shotgun",
            AbilitySpeed.ENDS_TURN,
            4,
            null,
            null,
            3,
            "Requires reloading after every shot!  Gains +1 damage for every tile closer to the target" +
                    " you are than the max range.",
            AbilityClass.TARGETED_ATTACK_ABILITY,
            allowsTargetingSelf = false,
            requirement = MustNotHaveEmptyClipAttribute(),
            abilityEffects = listOf(AddEmptyClipAttribute()),
            landingActor = null,
            projectileActor = null
            )
}

class AddEmptyClipAttribute : LogicalAbilityEffect {
    val actionManager: ActionManager by LazyInject(ActionManager::class.java)
    val attributeActionManager: AttributeActionManager by LazyInject(AttributeActionManager::class.java)
    override fun runAction(characterUsing: LogicalCharacter, tileLocationTargeted: TileLocation) {
        attributeActionManager.applyAttribute(characterUsing, EmptyClipAttribute())
    }

}

class MustNotHaveEmptyClipAttribute: ContextualAbilityRequirement {
    override fun canUseAbility(characterUsing: LogicalCharacter): Boolean {
        return !characterUsing.hasAttribute(EmptyClipAttribute())
    }

}

class EmptyClipAttribute : LogicalCharacterAttribute(
        "Empty Clip", imageIcon = SuperimposedTilemaps.toDefaultProtoActor(),
        description = {"Can't use a shotgun 'till you've reloaded!"},
        enablesAbility = "RELOAD_SHOTGUN") {

}


fun reloadShotgun(): LogicalAbility {
    return LogicalAbility("Reload Shotgun",
            AbilitySpeed.ENDS_TURN,
            0,
            null,
            null,
            0,
            "Reloads the shotgun.",
            AbilityClass.TARGETED_ATTACK_ABILITY,
            allowsTargetingSelf = false,
            id = "RELOAD_SHOTGUN",
            landingActor = null,
            projectileActor = null
            )
}
