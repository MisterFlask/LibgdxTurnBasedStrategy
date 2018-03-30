package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Created by Aaron on 3/27/2018.
 */

val lowAlpha = .1f
val highAlpha = .9f
val alphaDuration = .5f

fun highlightBlinking(): Action {
    return Actions.forever(
            Actions.sequence(
                    Actions.alpha(lowAlpha, alphaDuration),
                    Actions.delay(.1f),
                    Actions.alpha(highAlpha, alphaDuration)
            ))
}



