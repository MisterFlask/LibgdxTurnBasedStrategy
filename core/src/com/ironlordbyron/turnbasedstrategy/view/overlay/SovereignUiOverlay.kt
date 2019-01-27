package com.ironlordbyron.turnbasedstrategy.view.overlay

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import javax.inject.Inject


// When activated, brings up a "Sovereign Menu" that forces the player to hit a selection to do anything.
public class SovereignUiOverlay @Inject constructor(val stage: TacticalTiledMapStageProvider){

    // DEMO
    public fun display(){
        val dialog = object : Dialog("Warning", DEFAULT_SKIN, "dialog") {
            override fun result(obj: Any) {
                println("result $obj")
            }
        }
        dialog.text("Are you sure you want to quit?")
        dialog.button("Yes", true) //sends "true" as the result
        dialog.button("No", false)  //sends "false" as the result
        dialog.key(Input.Keys.ENTER, true) //sends "true" when the ENTER key is pressed
        dialog.show(stage.tiledMapStage)
    }
}