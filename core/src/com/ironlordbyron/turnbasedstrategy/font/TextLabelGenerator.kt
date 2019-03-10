package com.ironlordbyron.turnbasedstrategy.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ironlordbyron.turnbasedstrategy.common.wrappers.LabelWrapper
import com.ironlordbyron.turnbasedstrategy.common.wrappers.LabelWrapperImpl

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
    fun generateLabel(text: String): LabelWrapper{
        val font = FontGenerator.retrieveFont()
        val labelStyle = Label.LabelStyle()
        labelStyle.font = font

        val label = LabelWrapperImpl(text, labelStyle)
        return label
    }
}
