package com.ironlordbyron.turnbasedstrategy.view.animation.external

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.view.animation.ActivatableActor

class FloatingText(private val text: String, private val animationDuration: Long) : Actor(), ActivatableActor {
    var isActivatedAlready: Boolean = false
    init{
        this.isVisible = false
    }
    override fun activateIfInactive() {
        if (isActivatedAlready){
            return
        }
        this.isVisible = true
        animate()
        this.isActivatedAlready = true
    }
    var isAnimated = false
        private set
    private var animationStart: Long = 0
    private var deltaX: Float = 0.toFloat()
    private var deltaY: Float = 0.toFloat()

    private val font = BitmapFont()

    /**
     * @return true is the animation has finished.
     */
    private val isDisposable: Boolean
        get() = animationStart + animationDuration < System.currentTimeMillis()

    fun setDeltaX(deltaX: Float) {
        this.deltaX = deltaX
    }

    fun setDeltaY(deltaY: Float) {
        this.deltaY = deltaY
    }

    fun animate() {
        isAnimated = true
        animationStart = System.currentTimeMillis()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (isAnimated) {
            // The component will auto-destruct when animation is finished.
            if (isDisposable) {
                dispose()
                return
            }

            val elapsed = (System.currentTimeMillis() - animationStart).toFloat()

            // The text will be fading.
            font.setColor(color.r, color.g, color.b, parentAlpha * (1 - elapsed / animationDuration))

            font.draw(batch, text, x + deltaX * elapsed / 1000f, y + deltaY * elapsed / 1000f)
        }
    }

    /**
     * Dispose the component. **Note that all the children components also should
     * be disposed otherwise a memory leak will occur.**
     */
    private fun dispose() {
        font.dispose()
        remove()
    }
}