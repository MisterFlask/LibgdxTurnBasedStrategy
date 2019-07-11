package com.ironlordbyron.turnbasedstrategy.view.external_deprecated


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils

class DialogWord(private val font: BitmapFont, var word: String, a_effect: AppearEffect, private val effect: WordEffect, private val wColor: WordColor, private var x: Float, private var target_y: Float, line: Int) {
    var line = 0
    private var y: Float = 0.toFloat()
    private var target_x: Float = 0.toFloat()
    private var offset_x: Float = 0.toFloat()
    private var offset_y: Float = 0.toFloat()
    private var timer = 0.0f
    private var color: Color? = null
    private var targetColor: Color? = null
    private var scale = 1.0f
    private val targetScale = 1.0f

    enum class AppearEffect private constructor() {
        NONE, FADE_IN, GROW_IN, BUMP_IN
    }

    enum class WordEffect private constructor() {
        NONE, WAVY, SLOW_WAVY, SHAKY, PULSE
    }

    enum class WordColor private constructor() {
        DEFAULT, RED, GREEN, BLUE, GOLD, PURPLE, WHITE
    }

    init {
        if (gl == null) {
            gl = GlyphLayout()
        }
        this.y = target_y
        this.target_x = x
        this.targetColor = getColor()
        this.line = line
        this.color = Color(this.targetColor!!.r, this.targetColor!!.g, this.targetColor!!.b, 0.0f)
        if (effect == WordEffect.WAVY || effect == WordEffect.SLOW_WAVY) {
            this.timer = MathUtils.random(1.5707964f)
        }
        when (a_effect) {
            DialogWord.AppearEffect.FADE_IN -> {
            }
            DialogWord.AppearEffect.GROW_IN -> {
                this.y -= BUMP_OFFSET
                this.scale = 0.0f
            }
            DialogWord.AppearEffect.BUMP_IN -> this.y -= BUMP_OFFSET
        }
    }

    private fun getColor(): Color {
        when (this.wColor) {
            DialogWord.WordColor.RED -> return Settings.RED_TEXT_COLOR.cpy()
            DialogWord.WordColor.GREEN -> return Settings.GREEN_TEXT_COLOR.cpy()
            DialogWord.WordColor.BLUE -> return Settings.BLUE_TEXT_COLOR.cpy()
            DialogWord.WordColor.GOLD -> return Settings.GOLD_COLOR.cpy()
            DialogWord.WordColor.PURPLE -> return Settings.PURPLE_COLOR.cpy()
            DialogWord.WordColor.WHITE -> return Settings.CREAM_COLOR.cpy()
        }
        return Settings.CREAM_COLOR.cpy()
    }

    fun update() {
        if (this.x != this.target_x) {
            this.x = MathUtils.lerp(this.x, this.target_x, Gdx.graphics.deltaTime * 12.0f)
        }
        if (this.y != this.target_y) {
            this.y = MathUtils.lerp(this.y, this.target_y, Gdx.graphics.deltaTime * 12.0f)
        }
        this.color = this.color!!.lerp(this.targetColor!!, Gdx.graphics.deltaTime * 8.0f)
        if (this.scale != this.targetScale) {
            this.scale = MathHelper.scaleLerpSnap(this.scale, this.targetScale)
        }
        applyEffects()
    }

    private fun applyEffects() {
        when (this.effect) {
            DialogWord.WordEffect.SHAKY -> {
                this.timer -= Gdx.graphics.deltaTime
                if (this.timer < 0.0f) {
                    this.offset_x = MathUtils.random(-SHAKE_AMT, SHAKE_AMT)
                    this.offset_y = MathUtils.random(-SHAKE_AMT, SHAKE_AMT)
                    this.timer = 0.02f
                }
            }
            DialogWord.WordEffect.WAVY -> {
                this.timer += Gdx.graphics.deltaTime * 6.0f
                this.offset_y = Math.cos(this.timer.toDouble()).toFloat() * Settings.scale * 3.0f
            }
            DialogWord.WordEffect.SLOW_WAVY -> {
                this.timer += Gdx.graphics.deltaTime * 3.0f
                this.offset_y = Math.cos(this.timer.toDouble()).toFloat() * Settings.scale * 1.5f
            }
        }
    }

    fun fadeOut() {
        this.targetColor = Color(0.0f, 0.0f, 0.0f, 0.0f)
    }

    fun dialogFadeOut() {
        this.targetColor = Color(0.0f, 0.0f, 0.0f, 0.0f)
        this.target_y -= DIALOG_FADE_Y
    }

    fun shiftY(shiftAmount: Float) {
        this.target_y += shiftAmount
    }

    fun shiftX(shiftAmount: Float) {
        this.target_x += shiftAmount
    }

    fun setX(newX: Float) {
        this.target_x = newX
    }

    fun render(sb: SpriteBatch) {
        this.font.color = this.color
        this.font.data.setScale(this.scale)
        this.font.draw(sb, this.word, this.x + this.offset_x, this.y + this.offset_y)
        this.font.data.setScale(1.0f)
    }

    fun render(sb: SpriteBatch, y2: Float) {
        this.font.color = this.color
        this.font.data.setScale(this.scale)
        this.font.draw(sb, this.word, this.x + this.offset_x, this.y + this.offset_y + y2)
        this.font.data.setScale(1.0f)
    }

    companion object {
        private val BUMP_OFFSET = 20.0f * Settings.scale
        private var gl: GlyphLayout? = null
        private val COLOR_LERP_SPEED = 8.0f
        private val SHAKE_AMT = 2.0f * Settings.scale
        private val DIALOG_FADE_Y = 50.0f * Settings.scale
        private val WAVY_SPEED = 6.0f
        private val WAVY_DIST = 3.0f
        private val SHAKE_INTERVAL = 0.02f

        fun identifyWordEffect(word: String): WordEffect {
            if (word.length > 2) {
                if (word[0] == '@' && word[word.length - 1] == '@') {
                    return WordEffect.SHAKY
                }
                if (word[0] == '~' && word[word.length - 1] == '~') {
                    return WordEffect.WAVY
                }
            }
            return WordEffect.NONE
        }

        fun identifyWordColor(word: String): WordColor {
            if (word[0] == '#') {
                when (word[1]) {
                    'r' -> return WordColor.RED
                    'g' -> return WordColor.GREEN
                    'b' -> return WordColor.BLUE
                    'y' -> return WordColor.GOLD
                    'p' -> return WordColor.PURPLE
                }
            }
            return WordColor.DEFAULT
        }
    }
}
