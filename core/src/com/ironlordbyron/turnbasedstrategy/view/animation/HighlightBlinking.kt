package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * Created by Aaron on 3/27/2018.
 */

val lowAlpha = .1f
val highAlpha = .9f
val alphaDuration = .5f

fun foreverHighlightBlinking(): Action {
    return Actions.forever(
            temporaryHighlightBlinking()
            )
}

fun temporaryHighlightBlinking(): Action {
            return Actions.sequence(
                    Actions.alpha(lowAlpha, alphaDuration),
                    Actions.delay(.1f),
                    Actions.alpha(highAlpha, alphaDuration)
            )
}



