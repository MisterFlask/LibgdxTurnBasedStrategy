package com.ironlordbyron.turnbasedstrategy.view.animation

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import javax.inject.Singleton


@Singleton
class SpriteSheetParser(){

    companion object{
        val INSTANCE = SpriteSheetParser()
    }

    val cache = HashMap<DataDrivenOnePageAnimation, Array<TextureRegion>>()

    public fun createAnimation(anim: DataDrivenOnePageAnimation,
                               frameDuration: Float): Animation<TextureRegion> {

        val walkFrames = retrieveTextureRegionFromAnimation(anim)

        // Initialize the Animation with the frame interval and array of frames
        val walkAnimation = Animation<TextureRegion>(frameDuration, walkFrames)
        return walkAnimation
    }

    private fun retrieveTextureRegionFromAnimation(anim: DataDrivenOnePageAnimation): Array<TextureRegion> {
        if (cache[anim] != null){
            return cache[anim]!!
        }
        val frameRows = anim.rows
        val frameCols = anim.cols
        val walkSheet = Texture(Gdx.files.internal(anim.filePath))

        // Use the split utilityOrVest method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        val tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / frameCols,
                walkSheet.getHeight() / frameRows)

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        val walkFrames = Array<TextureRegion>(frameCols * frameRows)
        for (i in 0 until frameRows) {
            for (j in 0 until frameCols) {
                walkFrames.add(tmp[i][j])
            }
        }
        cache[anim] = walkFrames
        return walkFrames
    }
}