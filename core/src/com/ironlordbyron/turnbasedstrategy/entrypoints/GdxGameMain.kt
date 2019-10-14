package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.ironlordbyron.turnbasedstrategy.common.campaign.ui.CharacterSelectionScreen
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AnimationActionQueueProvider
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGuiEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.view.VictoryScreen
import com.ironlordbyron.turnbasedstrategy.view.animation.ActionRunner


class GdxGameMain : Game(), EventListener {

    val animationActionQueueProvider by LazyInject(AnimationActionQueueProvider::class.java)
    var mainMenuScreen: Screen? = null
    var tacticsScreen: Screen? = null
    val charSelectScreen by LazyInject(CharacterSelectionScreen::class.java)
    val victoryScreen by LazyInject(VictoryScreen::class.java)
    val actionRunner by LazyInject(ActionRunner::class.java)

    override fun create() {
        GameModuleInjector.getEventNotifier().registerGuiListener(this)
        mainMenuScreen = GameModuleInjector.generateInstance(MainMenuScreen::class.java)
        tacticsScreen = GameModuleInjector.generateInstance(TacticalMapScreen::class.java)
        GameModuleInjector.generateInstance(AutoInjector::class.java).instantiateAutoinjectables()
        this.screen = mainMenuScreen

        this.screen.show()
    }

    override fun render() {
        animationActionQueueProvider.kickOffQueueIfNotRunning()
        super.render()
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
            is TacticalGuiEvent.SwapToCharacterSelectScreen -> {
                this.screen = charSelectScreen
                charSelectScreen.initializeCharacterSelectionScreen(event.scenarioParams)
                this.screen.show()
            }
            is TacticalGuiEvent.SwapToVictoryScreen -> {
                log("Swapping to victory screen")
                this.screen = victoryScreen
                this.victoryScreen.populateMasterTable()
                this.screen.show()
            }
        }
    }
}

fun Any.log(msg: String){
    println("${this.javaClass.simpleName}:$msg")
}
fun Any.error(msg: String){
    println("ERROR:${this.javaClass.simpleName}:$msg")
}
