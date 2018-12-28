package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * Created by Aaron on 3/27/2018.
 */

val lowAlpha = .1f
val highAlpha = .5f
val defaultAlphaDuration = .5f

fun foreverHighlightBlinking(): Action {
    return Actions.forever(
            temporaryHighlightBlinking()
            )
}

fun temporaryHighlightBlinking(alphaDuration: Float = defaultAlphaDuration): Action {
            return Actions.sequence(
                    Actions.alpha(highAlpha, alphaDuration),
                    Actions.alpha(lowAlpha, alphaDuration)
            )
}



