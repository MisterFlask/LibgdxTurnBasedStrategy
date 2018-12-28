package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.GameCameraProvider
import javax.inject.Inject

public class CameraMovementAnimationGenerator @Inject constructor(val cameraProvider: GameCameraProvider){
    fun generateCameraMovementActionToLookAt(tileLocation: TileLocation): ActorActionPair{
        val actor = Actor()
        val action = Actions.delay(.5f)

        return ActorActionPair(actor, action)
    }

    fun getDesiredPositionOfCameraForActionAtLocation(){

    }
}

public class CameraMovementAction(val maxSeconds: Float,
                                  val camera: Camera,
                                  val desiredPosition: Vector3): Action(){


    var totalTimeInSeconds = 0f
    override fun act(delta: Float): Boolean {
        totalTimeInSeconds += delta
        if (totalTimeInSeconds > maxSeconds){
            return true
        }
        return false
    }

}