package com.ironlordbyron.turnbasedstrategy.view.external


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable

abstract class AbstractGameEffect : Disposable {
    var duration: Float = 0.toFloat()
    var startingDuration: Float = 0.toFloat()
    protected var color: Color? = null
    var isDone = false
    protected var scale = Settings.scale
    protected var rotation = 0.0f
    var renderBehind = false

    open fun update() {
        this.duration -= Gdx.graphics.deltaTime
        if (this.duration < this.startingDuration / 2.0f) {
            this.color!!.a = this.duration / (this.startingDuration / 2.0f)
        }
        if (this.duration < 0.0f) {
            this.isDone = true
            this.color!!.a = 0.0f
        }
    }

    abstract fun render(paramSpriteBatch: SpriteBatch)

    fun render(sb: SpriteBatch, x: Float, y: Float) {}

    abstract override fun dispose()
}
