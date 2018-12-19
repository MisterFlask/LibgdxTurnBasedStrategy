package com.ironlordbyron.turnbasedstrategy.view.animation.camera

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class Rumbler @Inject constructor(val cameraProvider: GameCameraProvider) : RunsOnRender, EventListener {
    init{
        cameraProvider.register(this)
    }

    override fun render(camera: Camera) {
        onRender(camera)
    }

    fun onRender(camera: Camera){
        if (_RumbleEffect.rumbleTimeLeft > 0){
            _RumbleEffect.tick(Gdx.graphics.getDeltaTime());
            camera.translate(_RumbleEffect.pos);
        }
    }

    fun executeRumble(rumblePower: Float, rumbleLength: Float){
        _RumbleEffect.rumble(rumblePower, rumbleLength)
    }
}