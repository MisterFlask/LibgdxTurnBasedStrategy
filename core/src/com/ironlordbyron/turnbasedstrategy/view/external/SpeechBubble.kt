package com.ironlordbyron.turnbasedstrategy.view.external


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.megacrit.cardcrawl.core.Settings
class SpeechBubble(x: Float, y: Float, duration: Float, msg: String, isPlayer: Boolean) : AbstractGameEffect() {
    private var shadow_offset = 0.0f
    private var x: Float = 0.toFloat()
    private val y: Float
    private var scale_x: Float = 0.toFloat()
    private var scale_y: Float = 0.toFloat()
    private var wavy_y: Float = 0.toFloat()
    private var wavyHelper: Float = 0.toFloat()
    private var scaleTimer = 0.3f
    private val facingRight: Boolean

    constructor(x: Float, y: Float, msg: String, isPlayer: Boolean) : this(x, y, 2.0f, msg, isPlayer) {}

    init {
        var effect_x = -170.0f * Settings.scale
        if (isPlayer) {
            effect_x = 170.0f * Settings.scale
        }
        EffectsQueue.add(SpeechTextEffect(x + effect_x, y + 124.0f * Settings.scale, duration, msg, DialogWord.AppearEffect.BUMP_IN))
        if (isPlayer) {
            this.x = x + ADJUST_X
        } else {
            this.x = x - ADJUST_X
        }
        this.y = y + ADJUST_Y
        this.scaleTimer = 0.3f
        this.color = Color(0.8f, 0.9f, 0.9f, 0.0f)
        this.duration = duration
        this.facingRight = !isPlayer
    }

    override fun update() {
        updateScale()

        this.wavyHelper += Gdx.graphics.deltaTime * WAVY_SPEED
        this.wavy_y = MathUtils.sin(this.wavyHelper) * WAVY_DISTANCE

        this.duration -= Gdx.graphics.deltaTime
        if (this.duration < 0.0f) {
            this.isDone = true
        }
        if (this.duration > 0.3f) {
            this.color!!.a = MathUtils.lerp(this.color!!.a, 1.0f, Gdx.graphics.deltaTime * 12.0f)
        } else {
            this.color!!.a = MathUtils.lerp(this.color!!.a, 0.0f, Gdx.graphics.deltaTime * 12.0f)
        }
        this.shadow_offset = MathUtils.lerp(this.shadow_offset, SHADOW_OFFSET, Gdx.graphics.deltaTime * 4.0f)
    }

    private fun updateScale() {
        this.scaleTimer -= Gdx.graphics.deltaTime
        if (this.scaleTimer < 0.0f) {
            this.scaleTimer = 0.0f
        }
        this.scale_x = Interpolation.circleIn.apply(Settings.scale, Settings.scale * 0.5f, this.scaleTimer / 0.3f)
        this.scale_y = Interpolation.swingIn.apply(Settings.scale, Settings.scale * 0.8f, this.scaleTimer / 0.3f)
    }

    override fun render(sb: SpriteBatch) {
        sb.color = Color(0.0f, 0.0f, 0.0f, this.color!!.a / 4.0f)
        sb.draw(ImageMaster.SPEECH_BUBBLE_IMG, this.x - 256.0f + this.shadow_offset, this.y - 256.0f + this.wavy_y - this.shadow_offset, 256.0f, 256.0f, 512.0f, 512.0f, this.scale_x, this.scale_y, this.rotation, 0, 0, 512, 512, this.facingRight, false)

        sb.setColor(this.color)
        sb.draw(ImageMaster.SPEECH_BUBBLE_IMG, this.x - 256.0f, this.y - 256.0f + this.wavy_y, 256.0f, 256.0f, 512.0f, 512.0f, this.scale_x, this.scale_y, this.rotation, 0, 0, 512, 512, this.facingRight, false)
    }

    override fun dispose() {}

    companion object {
        private val RAW_W = 512
        private val SHADOW_OFFSET = 16.0f * Settings.scale
        private val WAVY_SPEED = 6.0f * Settings.scale
        private val WAVY_DISTANCE = 2.0f * Settings.scale
        private val SCALE_TIME = 0.3f
        private val ADJUST_X = 170.0f * Settings.scale
        private val ADJUST_Y = 116.0f * Settings.scale
        private val DEFAULT_DURATION = 2.0f
        private val FADE_TIME = 0.3f
    }
}
