package com.ironlordbyron.turnbasedstrategy.view.animation.animationlisteners

import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.FunctionalCharacterAttributeFactory
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.GameEventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.DeathAnimationGenerator
import javax.inject.Inject

public class DeathGameEventListener @Inject constructor(val gameEventNotifier: GameEventNotifier,
                                                        val animationActionQueueProvider: AnimationActionQueueProvider,
                                                        val logicHooks: LogicHooks,
                                                        val deathAnimationGenerator: DeathAnimationGenerator) : GameEventListener{
    override fun consumeGameEvent(tacticalGameEvent: TacticalGameEvent) {
        when(tacticalGameEvent){
            is TacticalGameEvent.UnitKilled -> handleUnitKilledEvent(tacticalGameEvent.character)
        }
    }

    private fun handleUnitKilledEvent(targetCharacter: LogicalCharacter) {
        // animation
        animationActionQueueProvider.addAction(deathAnimationGenerator.turnCharacterSideways(targetCharacter))

        // game event
        logicHooks.onDeath(targetCharacter)
    }

    init{
        gameEventNotifier.registerGameListener(this)
    }


}