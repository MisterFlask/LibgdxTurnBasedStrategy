package com.ironlordbyron.turnbasedstrategy.view.animation.camera

import com.badlogic.gdx.graphics.Camera
import javax.inject.Singleton


interface RunsOnRender{
    fun render(camera: Camera)
}

@Singleton
class GameCameraProvider(){
    lateinit var camera: Camera

    val toRunOnRender = ArrayList<RunsOnRender>()
    fun register(runsOnRender: RunsOnRender){
        toRunOnRender.add(runsOnRender)
    }

    fun render(){
        toRunOnRender.forEach{it.render(camera)}
    }

}
