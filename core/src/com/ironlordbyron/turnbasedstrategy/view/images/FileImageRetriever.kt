package com.ironlordbyron.turnbasedstrategy.view.images

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

enum class Icon(val imagePath: String){
    ASSAULT("trample.png"),
    CHARGE("electric.png"),
    RESCUE ("hand.png"),
    RARROW("rarrow.png")

}

public class FileImageRetriever{

    public fun retrieveIconImage(icon: Icon): Texture {
         return Texture(Gdx.files.internal("icons/${icon.imagePath}" ));
    }
    public fun retrieveIconImageAsSprite(icon: Icon, dimensions: Dimensions, color: Color?): Sprite {
        val iconImage = retrieveIconImage(icon)
        val textureRegion = TextureRegion(iconImage)
        val sprite = Sprite(textureRegion);
        if (color != null){
            sprite.color = color
        }
        return sprite
    }
    public fun retrieveIconImageAsDrawable(icon: Icon, dimensions: Dimensions, color: Color? = null): Drawable {
        val iconImage = retrieveIconImage(icon)
        val textureRegion = TextureRegion(iconImage)
        var sprite = TextureRegionDrawable(textureRegion);
        // minwidth and minheight are the appropriate ways to set image button boundaries.
        sprite.minHeight = dimensions.height.toFloat()
        sprite.minWidth = dimensions.width.toFloat()
        if (color != null){
            return sprite.tint(color)
        }
        return sprite
    }
}

public data class Dimensions(val width: Int, val height: Int)