package com.ironlordbyron.turnbasedstrategy.view.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.ironlordbyron.turnbasedstrategy.view.images.fromFileToTexture


private val orangeSheetWindow = "ui/_sheet_window_12.png".fromFileToTexture()
private val blackSheetWindow = "ui/_sheet_window_20.png".fromFileToTexture()
private val greySheetWindow = "ui/_sheet_window_04.png".fromFileToTexture()
private val greenSheetWindow = "ui/_sheet_window_11.png".fromFileToTexture()
private val redSheetWindow = "ui/_sheet_window_10.png".fromFileToTexture()

val greenNinePatch by lazy{
    getNinepatchFromSheetWindow(greenSheetWindow)
}
val redNinePatch by lazy{
    getNinepatchFromSheetWindow(redSheetWindow)
}

val blackNinepatch by lazy{
    getNinepatchFromSheetWindow(blackSheetWindow)
}
val orangeNinepatch by lazy{
    getNinepatchFromSheetWindow(orangeSheetWindow)
}
val greyNinePatch by lazy{
    getNinepatchFromSheetWindow(greySheetWindow)
}
val goldBorderBlackBackgroundNinepatch by lazy{
    getNinepatchFromSheetWindow( "ui/_sheet_window_19.png".fromFileToTexture())
}

private fun getNinepatchFromSheetWindow(sheetWindow: Texture): NinePatch{
    // top left image is 48/48
    val largeBox = TextureRegion(sheetWindow, 0, 0, 48, 48)
    val ninepatch = NinePatch(largeBox, 9, 9,9 , 9)
    return ninepatch
}
fun Table.withGoldBorderBlackBackground(scale: Float? = 1f){
    this.withBorder(null, scale, goldBorderBlackBackgroundNinepatch)
}

fun Table.withOrangeBorder(scale: Float? = 1f){
    this.withBorder(null, scale, orangeNinepatch)
}
fun Table.withGreyBackground(scale: Float? = 1f){
    this.withBorder(null, scale, greyNinePatch)
}

fun Table.withBorder(color: Color? = null,
                     scale: Float? = 1f,
                     ninePatch: NinePatch = blackNinepatch) : Table{
    val ninepatchCopy = NinePatch(ninePatch)
    if (scale != null){
        ninepatchCopy.scale(scale, scale)
    }
    val background = NinePatchDrawable(ninepatchCopy)
    if (color != null){
        background.patch.color = color
    }
    this.background = background
    return this
}