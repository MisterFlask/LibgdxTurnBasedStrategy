package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.ironlordbyron.turnbasedstrategy.common.wrappers.RenderingFunction
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.images.Dimensions
import com.ironlordbyron.turnbasedstrategy.view.ui.DEFAULT_SKIN
import com.ironlordbyron.turnbasedstrategy.view.ui.addLabel
import com.kotcrab.vis.ui.building.utilities.Alignment

data class CharacterDisplayParameters(val portraitDimensions: Dimensions)

class CharacterDisplayUiElement : Table() {

    var selectedCharacter: TacMapUnitTemplate? = null
    var portraitDimensions: Dimensions = Dimensions(100,100)
    var iconDimensions = Dimensions(55,55)

    val characterImageManager by LazyInject(CharacterImageManager::class.java)


    fun buildCharacterDisplayTable() : Table{
        val table = Table()
        regenerateCharacterDisplayTable()
        return table
    }

    fun regenerateCharacterDisplayTable() {
        this.clearChildren()
        val selectedCharacter = selectedCharacter
        if (selectedCharacter != null) {
            this.align(Alignment.LEFT.alignment)
            this.add(Label(selectedCharacter.templateName, DEFAULT_SKIN, "title"))
            this.row()
            this.add(characterImageManager.retrieveCharacterTemplateImage(selectedCharacter).actor)
                    .size(portraitDimensions.width.toFloat(), portraitDimensions.height.toFloat())
            this.row()
            this.add(displayCharacterHp(selectedCharacter))
            this.row()
            this.add(displayCharacterAttributes(selectedCharacter)).left()
            this.row()
            val turnStartAction = selectedCharacter.turnStartAction
            if (turnStartAction != null){
                this.addLabel("AT TURN START")
                this.row()
                this.addLabel(turnStartAction.displayName, tooltip = turnStartAction.extendedDescription)
                this.row()
                this.addLabel("[" + turnStartAction.cooldownDescription + "]")
            }
        }
    }

    private fun displayCharacterHp(selectedCharacter: TacMapUnitTemplate): Label {
        return Label("HP: ${selectedCharacter.healthLeft}/${selectedCharacter.maxHealth}", DEFAULT_SKIN)
    }

    fun displayCharacterAttributes(selectedCharacter: TacMapUnitTemplate): Table{
        val table = Table(DEFAULT_SKIN)
        for (item in selectedCharacter.getAttributes()){
            val attrImage = item.logicalAttribute.imageIcon.toActor(AnimatedImageParams.RUN_ALWAYS_AND_FOREVER.copy(hittable = true))
            attrImage.addTooltip(RenderingFunction.simple(item.logicalAttribute.description(item.stacks)))
            table.add(attrImage.actor).width(iconDimensions.width.toFloat()).height(iconDimensions.height.toFloat())
            if (item.stacks > 1) {
                table.add(Label("[${item.stacks.toString()}]", DEFAULT_SKIN))
            }
            val label = Label(item.logicalAttribute.name, DEFAULT_SKIN)
            label.setWrap(true)
            table.add(label)
            table.row()
        }
        return table
    }

}

