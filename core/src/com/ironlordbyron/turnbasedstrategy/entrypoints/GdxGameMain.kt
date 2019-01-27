package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector

class GdxGameMain : Game(), EventListener {
    var mainMenuScreen: Screen? = null
    var tacticsScreen: Screen? = null

    override fun create() {

        GameModuleInjector.getEventNotifier().registerGuiListener(this)
        mainMenuScreen = MainMenuScreen()
        tacticsScreen = TacticalMapScreen()
        this.screen = mainMenuScreen

        this.screen.show()
    }

    override fun consumeGuiEvent(event: TacticalGuiEvent) {
        when(event){
            is TacticalGuiEvent.SwapToTacticsScreen -> {
                this.screen = tacticsScreen
                this.screen.show()
            }
            is TacticalGuiEvent.SwapToMainMenu -> {
                this.screen = mainMenuScreen
                this.screen.show()
            }
        }
    }
}