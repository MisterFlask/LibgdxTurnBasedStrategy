package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ActorWrapper
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.ImageWrapper

val IMAGE_ICON_CACHE = HashMap<String, Texture>()

public class ImageIcon (
        val folder : String,
        val filename : String,
        override val orientation: OrientationType = OrientationType.NEUTRAL) : ProtoActor{
    companion object {
        val _ICON_FOLDER = "icons"
        val _PAINTERLY_FOLDER = "icons/painterly"

    }
    override fun toActorWrapper(animatedImageParams: AnimatedImageParams): ActorWrapper {
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

class PainterlyIcon(val fileString: String) {
    fun toProtoActor(extremitude: Int): ProtoActor {
        assert(extremitude in 1 .. 4)
        return ImageIcon(ImageIcon._PAINTERLY_FOLDER, fileString.replace("[x]", extremitude.toString()))
    }
}

private fun String.toPainterlyIcon(): PainterlyIcon{
    return PainterlyIcon(this)
}

public object PainterlyIcons{
    val FIRE_ARROWS = "fire-arrows-[x].png".toPainterlyIcon()
    val FOG_ACID = "fog-acid-[x].png".toPainterlyIcon()
    val FOG_AIR = "fog-air-[x].png".toPainterlyIcon()
    val FOG_SKY = "fog-sky-[x].png".toPainterlyIcon()
    val FOG_ORANGE = "fog-orange-[x].png".toPainterlyIcon()
    val BEAM_ORANGE = "beam-orange-[x].png".toPainterlyIcon()
    val HEAL_JADE = "heal-jade-[x].png".toPainterlyIcon()
    val PROTECT_SKY = "protect-sky-[x].png".toPainterlyIcon()
    val LINK_BLUE = "link-blue-[x].png".toPainterlyIcon()
    val LIGHT_ROYAL = "light-royal-[x].png".toPainterlyIcon()
}

val iconBorderFolder = "icons/borders"
private fun String.toPainterlyBorder(): ImageIcon {
    return ImageIcon(iconBorderFolder, this)
}

public object PainterlyBorders{
    val greenFrame = "frame-0-acid.png".toPainterlyBorder()
    val blueFrame = "frame-0-blue.png".toPainterlyBorder()
}



public class SpritesheetIconPage(val filePath: String,
                                 val rows: Int,
                                 val cols: Int){

    val frames = splitIntoFrames()

    private fun splitIntoFrames(): List<TextureRegion> {

        val frameRows = rows
        val frameCols = cols
        val walkSheet = Texture(Gdx.files.internal(filePath))

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
        return walkFrames.toList()
    }
}