package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.ImageWrapper

val IMAGE_ICON_CACHE = HashMap<String, Texture>()

public class ImageIcon (
        val folder : String,
        val filename : String,
        override val orientation: OrientationType = OrientationType.NEUTRAL) : ProtoActor{
    companion object {
        val ICON_FOLDER = "icons"
        val PAINTERLY_FOLDER = "icons/painterly"
    }
    override fun toActor(animatedImageParams: AnimatedImageParams): ActorWrapper {
        val fileTexture = retrieveIconImage(folder, filename)
        return ImageWrapper(texture = TextureRegion(fileTexture),
                hittable = true)
    }

    public fun retrieveIconImage(folder: String, filename: String): Texture {
        val path = "${folder}/${filename}"
        if (IMAGE_ICON_CACHE.containsKey(path)){
            return IMAGE_ICON_CACHE[path]!!
        }
        val tex = Texture(Gdx.files.internal(path));
        IMAGE_ICON_CACHE.put(path, tex)
        return tex
    }
}