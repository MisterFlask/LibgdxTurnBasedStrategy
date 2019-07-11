package com.ironlordbyron.turnbasedstrategy.view.external_deprecated


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.ironlordbyron.turnbasedstrategy.font.FontGenerator
import java.util.ArrayList
import java.util.Scanner

class SpeechTextEffect(private val x: Float, private val y: Float,
                       duration: Float,
                       msg: String, private val a_effect: DialogWord.AppearEffect) : AbstractGameEffect() {
    private val font: BitmapFont
    private var wordTimer = 0.0f
    private var textDone = false
    private val words = ArrayList<SpeechWord>()
    private var curLine = 0
    private val s: Scanner
    private var curLineWidth = 0.0f


    init {
        if (gl == null) {
            gl = GlyphLayout()
        }
        this.duration = duration
        this.font = FontGenerator.retrieveFont() //TODO: FontHelper.speech_font
        this.s = Scanner(msg)
    }

    override fun update() {
        this.wordTimer -= Gdx.graphics.deltaTime
        if (this.wordTimer < 0.0f && !this.textDone) {
            addWord()
        }
        for (w in this.words) {
            w.update()
        }
        this.duration -= Gdx.graphics.deltaTime
        if (this.duration < 0.0f) {
            this.words.clear()
            this.isDone = true
        }
        if (this.duration < 0.3f) {
            for (w in this.words) {
                w.fadeOut()
            }
        }
    }

    private fun addWord() {
        this.wordTimer = 0.03f
        if (this.s.hasNext()) {
            var word = this.s.next()
            if (word == "NL") {
                this.curLine += 1
                for (w in this.words) {
                    w.shiftY(LINE_SPACING)
                }
                this.curLineWidth = 0.0f
                return
            }
            val color = SpeechWord.identifyWordColor(word)
            if (color !== DialogWord.WordColor.DEFAULT) {
                word = word.substring(2, word.length)
            }
            val effect = SpeechWord.identifyWordEffect(word)
            if (effect !== DialogWord.WordEffect.NONE) {
                word = word.substring(1, word.length - 1)
            }
            gl!!.setText(this.font, word)
            var temp = 0.0f
            if (this.curLineWidth + gl!!.width > DEFAULT_WIDTH) {
                this.curLine += 1
                for (w in this.words) {
                    w.shiftY(LINE_SPACING)
                }
                this.curLineWidth = gl!!.width + CHAR_SPACING
                temp = -this.curLineWidth / 2.0f
            } else {
                this.curLineWidth += gl!!.width
                temp = -this.curLineWidth / 2.0f
                for (w in this.words) {
                    if (w.line == this.curLine) {
                        w.setX(this.x + temp)
                        gl!!.setText(this.font, w.word)
                        temp += gl!!.width + CHAR_SPACING
                    }
                }
                this.curLineWidth += CHAR_SPACING
                gl!!.setText(this.font, "$word ")
            }
            this.words.add(SpeechWord(this.font, word, this.a_effect, effect, color, this.x + temp, this.y - LINE_SPACING * this.curLine, this.curLine))
        } else {
            this.textDone = true
            this.s.close()
        }
    }

    override fun render(sb: SpriteBatch) {
        for (w in this.words) {
            w.render(sb)
        }
    }

    override fun dispose() {
        for (w in this.words) {
            w.dispose()
        }
    }

    companion object {
        private var gl: GlyphLayout? = null
        private val DEFAULT_WIDTH = 280.0f * Settings.scale
        private val LINE_SPACING = 15.0f * Settings.scale
        private val CHAR_SPACING = 8.0f * Settings.scale
        private val WORD_TIME = 0.03f
        private val FADE_TIME = 0.3f
    }
}
