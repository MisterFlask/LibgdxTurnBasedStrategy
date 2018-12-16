package com.ironlordbyron.turnbasedstrategy.view.images

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.SpriteActor
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject

enum class Icon(val imagePath: String, val imageFolder: String){
    ASSAULT("trample.png", "icons"),
    CHARGE("electric.png", "icons"),
    RESCUE ("hand.png", "icons"),
    RARROW("rarrow.png", "icons"),
    TARGETING_CURSOR("Ardentryst-target.png", "cursors"),
    ATTACK_CURSOR("Ardentryst-target2.png", "cursors")
}

public class FileImageRetriever @Inject constructor(val tileMapProvider: TileMapProvider){

    public fun retrieveIconImage(icon: Icon): Texture {
         return Texture(Gdx.files.internal("${icon.imageFolder}/${icon.imagePath}" ));
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

    public fun retrieveIconImageAsActor(icon: Icon, tileLocation: TileLocation): Actor {
        val iconImage = retrieveIconImage(icon)
        val textureRegion = TextureRegion(iconImage)
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        val actor = SpriteActor(textureRegion, boundingBox)
        actor.width =boundingBox.width.toFloat()
        actor.height = boundingBox.height.toFloat()

        return actor
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