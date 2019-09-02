package com.ironlordbyron.turnbasedstrategy.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ironlordbyron.turnbasedstrategy.common.wrappers.LabelWrapper
import com.ironlordbyron.turnbasedstrategy.common.wrappers.LabelWrapperImpl
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import java.awt.Dimension

object FontGenerator{
    fun retrieveFont(fontScale: Float? = null): BitmapFont{
        val fontFile = Gdx.files.internal("fonts/littera_default/font.fnt")
        val picFile = Gdx.files.internal("fonts/littera_default/font.png")
        val font = BitmapFont(fontFile, picFile, false)
        if (fontScale != null){
            font.data.setScale(fontScale)
        }
        return font
    }
}

class TextLabelGenerator{
    fun generateLabel(text: String,
                      dimensions: Dimensions? = null,
                      scale: Float?= null,
                      hittable: Boolean = true): LabelWrapper{
        val font = FontGenerator.retrieveFont(scale)
        val labelStyle = Label.LabelStyle()
        labelStyle.font = font

        val label = LabelWrapperImpl(text, labelStyle, hittable)
        if (dimensions != null){
            label.label.setSize(dimensions.width.toFloat(), dimensions.height.toFloat())
        }
        label.label.setWrap(true)
        return label
    }
}
