package com.ironlordbyron.turnbasedstrategy.controller

import com.ironlordbyron.turnbasedstrategy.common.CharacterTemplates
import com.ironlordbyron.turnbasedstrategy.common.GameBoardOperator
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.TileLocation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Aaron on 3/24/2018.
 */
@Singleton
class TacticalMapController @Inject constructor(val gameBoardOperator: GameBoardOperator,
                                                eventNotifier: EventNotifier) : EventListener {
    override fun consumeEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.TileClicked -> playerClickedOnTile(event.tileLocation)
        }
    }

    init{
        eventNotifier.registerListener(this)
    }

    fun playerClickedOnTile(location: TileLocation){
        println("Tac map controller registering click at $location")
        gameBoardOperator.addCharacterToTile(CharacterTemplates.DEFAULT_TEMPLATE, location)
    }
}