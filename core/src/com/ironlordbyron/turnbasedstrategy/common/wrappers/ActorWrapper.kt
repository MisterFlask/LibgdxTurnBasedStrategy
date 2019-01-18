package com.ironlordbyron.turnbasedstrategy.common.wrappers

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Actor


public interface ActorWrapper{
    val actor: Actor
    var shader: ShaderProgram?
}
