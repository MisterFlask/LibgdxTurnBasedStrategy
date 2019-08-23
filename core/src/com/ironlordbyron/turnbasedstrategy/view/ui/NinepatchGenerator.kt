package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.ironlordbyron.turnbasedstrategy.view.images.fromFileToTextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.ironlordbyron.turnbasedstrategy.view.images.fromFileToTexture


private val orangeSheetWindow = "ui/_sheet_window_12.png".fromFileToTexture()
private val blackSheetWindow = "ui/_sheet_window_20.png".fromFileToTexture()

val ninepatch by lazy{
    getNinepatchFromSheetWindow(blackSheetWindow)
}

private fun getNinepatchFromSheetWindow(sheetWindow: Texture): NinePatch{
    // top left image is 48/48
    val largeBox = TextureRegion(sheetWindow, 0, 0, 48, 48)
    val ninepatch = NinePatch(largeBox, 9, 9,9 , 9)
    return ninepatch
}

private val ninepatchImgWhite = "border/tinyborder.png".fromFileToTexture()
fun Table.withBorder(color: Color? = null,
                     scale: Float? = 1f) : Table{
    if (scale != null){
        ninepatch.scale(scale, scale)
    }
    val background = NinePatchDrawable(ninepatch)
    if (color != null){
        background.patch.color = color
    }
    this.background = background
    return this
}