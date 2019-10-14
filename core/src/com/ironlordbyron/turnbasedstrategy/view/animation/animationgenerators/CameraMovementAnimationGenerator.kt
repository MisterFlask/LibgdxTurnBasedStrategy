package com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.LogicalCharacterActorGroup
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.CameraConfig
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.GameCameraProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class CameraMovementAnimationGenerator @Inject constructor(val cameraProvider: GameCameraProvider,
                                                                  val logicalTileTracker: LogicalTileTracker){

    fun generateCameraMovementActionToLookAt(toLookAt: Actor): ActorActionPair{
        // HACK:   If the clickListeningActor is a child of a logical character group, we're just going to look at the logical character group.
        val actor = getCameraFocusTarget(toLookAt)
        val action = CameraMovementAction(CameraConfig.secondsForAutoCameraMove, cameraProvider.camera,
                getDesiredPositionOfCameraForActionAtLocation(actor))
        // println("Generated camera action")
        return ActorActionPair(actor, action, name = "CameraMovement", cameraTrigger = false)
    }

    private fun getCameraFocusTarget(toLookAt: Actor): Actor {
        var current: Actor? = toLookAt
        while(current!=null){
            if (current is LogicalCharacterActorGroup){
                return current
            }
            current = current.parent
        }
        return toLookAt
    }

    fun getDesiredPositionOfCameraForActionAtLocation(actor: Actor) : Vector3{
        return Vector3(actor.x,  actor.y, 0f)
    }
}

// Moves the camera such that we can clearly see the animation occurring in the queue.
public class CameraMovementAction(val maxSeconds: Float,
                                  val camera: OrthographicCamera,
                                  val desiredPosition: Vector3): Action(){

    val actorPosition: Vector3
    var originalZoom = camera.zoom
    init{
        actorPosition = desiredPosition
        desiredPosition.x = desiredPosition.x + CameraConfig.xOffsetForMainTacticsScreen

    }

    var totalTimeInSeconds = 0f
    override fun act(delta: Float): Boolean {
        if (totalTimeInSeconds == 0f){
            if (camera.frustum.pointInFrustum(actorPosition)){
                // we're just going to not do this if the clickListeningActor is already in position.
                return true
            }
            // first iteration
            originalZoom = camera.zoom
        }


        totalTimeInSeconds += delta

        val progress = totalTimeInSeconds / maxSeconds
        camera.position.lerp(desiredPosition, progress)
        camera.zoom = MathUtils.lerp(originalZoom, CameraConfig.zoomTo, progress)

        if (totalTimeInSeconds > maxSeconds){
            return true
        }
        return false
    }

}