package com.ironlordbyron.turnbasedstrategy.view.ui.external

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class BackgroundColor @JvmOverloads constructor(filename: String, x: Float = 0.0f, y: Float = 0.0f, width: Float = 0.0f, height: Float = 0.0f) : Drawable {

    private var x: Float? = null
    private var y: Float? = null
    private var width: Float? = null
    private var height: Float? = null

    var fillParent: Boolean? = null

    private var filename: String? = null
    private var texture: Texture? = null
    private var textureRegion: TextureRegion? = null
    private var sprite: Sprite? = null
    private var color: Color? = null

    init {
        this.setPosition(x, y)
        this.setSize(width, height)
        initialize(filename)
    }

    private fun initialize(filename: String) {
        this.filename = filename
        if (x == null || y == null)
            setPosition()    // x = 0.0f; y = 0.0f;
        val width = width
        val height = height
        if (width == null || height == null || width < 0.0f || height < 0.0f)
            setSize()        // viewPortWidth = 0.0f; viewportHeight = 0.0f;
        if (color == null)
            setColor(255, 255, 255, 255)
        if (sprite == null) {
            try {
                setSprite()
            } catch (e: Exception) {
                System.err.println(e)
            }

        }
        if (fillParent == null)
            fillParent = true
    }

    private fun setTexture() {
        if (texture != null)
            texture!!.dispose()
        texture = Texture(Gdx.files.internal(filename))
    }

    private fun setTextureRegion() {
        textureRegion = TextureRegion(texture, getWidth().toInt(), getHeight().toInt())
    }

    private fun setSprite() {
        if (texture == null)
            setTexture()
        setTextureRegion()
        sprite = Sprite(textureRegion!!)
        setSpriteColor()
    }

    private fun setSpriteColor() {
        sprite!!.setColor(color!!.r, color!!.g, color!!.b, color!!.a)
    }

    private fun setPosition() {
        this.x = 0.0f
        this.y = 0.0f
    }

    private fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    private fun setSize() {
        this.width = sprite!!.width
        this.height = sprite!!.height
    }

    private fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height
    }

    fun setColor(r: Int, g: Int, b: Int, a: Int) {
        color = Color(r / 255f, g / 255f, b / 255f, a / 255f)
    }

    fun setColor(r: Float, g: Float, b: Float, a: Float) {
        color = Color(r / 255f, g / 255f, b / 255f, a / 255f)
    }

    private fun setSpritePosition(x: Float, y: Float) {
        sprite!!.x = x
        sprite!!.y = y
    }

    private fun updateSprite(x: Float, y: Float, width: Float, height: Float) {
        if (fillParent!!) {
            setSpritePosition(x, y)
            if (width != textureRegion!!.regionWidth.toFloat() || height != textureRegion!!.regionHeight.toFloat()) {
                setSize(width, height)
                setSprite()
            }
        }
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        updateSprite(x, y, width, height)
        sprite!!.draw(batch)
    }

    override fun getLeftWidth(): Float {
        // TODO Auto-generated method stub
        return 0f
    }

    override fun setLeftWidth(leftWidth: Float) {
        // TODO Auto-generated method stub

    }

    override fun getRightWidth(): Float {
        // TODO Auto-generated method stub
        return 0f
    }

    override fun setRightWidth(rightWidth: Float) {
        // TODO Auto-generated method stub

    }

    override fun getTopHeight(): Float {
        // TODO Auto-generated method stub
        return 0f
    }

    override fun setTopHeight(topHeight: Float) {
        // TODO Auto-generated method stub

    }

    override fun getBottomHeight(): Float {
        // TODO Auto-generated method stub
        return 0f
    }

    override fun setBottomHeight(bottomHeight: Float) {
        // TODO Auto-generated method stub

    }

    override fun getMinWidth(): Float {
        // TODO Auto-generated method stub
        return 0f
    }

    override fun setMinWidth(minWidth: Float) {
        // TODO Auto-generated method stub

    }

    override fun getMinHeight(): Float {
        // TODO Auto-generated method stub
        return 0f
    }

    override fun setMinHeight(minHeight: Float) {
        // TODO Auto-generated method stub

    }

    fun getX(): Float {
        return x!!
    }

    fun setX(x: Float) {
        this.x = x
    }

    fun getY(): Float {
        return y!!
    }

    fun setY(y: Float) {
        this.y = y
    }

    fun getWidth(): Float {
        return width!!
    }

    fun setWidth(width: Float) {
        this.width = width
    }

    fun getHeight(): Float {
        return height!!
    }

    fun setHeight(height: Float) {
        this.height = height
    }

}