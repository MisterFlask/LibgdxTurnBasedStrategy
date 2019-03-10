package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute

public class AttributeOperator(val logicHooks: LogicHooks){

    private fun hasAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute): Boolean {
        return logicalCharacter.attributes.any{it.name == logicalCharacterAttribute.name} // TODO: Create ID field
    }

    fun applyAttribute(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute,
                       stacksToApply: Int = 1){
        if (hasAttribute(logicalCharacter, logicalCharacterAttribute) and !logicalCharacterAttribute.stackable){
            return
        }
        if (hasAttribute(logicalCharacter, logicalCharacterAttribute) and logicalCharacterAttribute.stackable){
            logicalCharacterAttribute.stacks += 1
        }
        if (!hasAttribute(logicalCharacter,logicalCharacterAttribute)){
            logicalCharacter.tacMapUnit.attributes.add(logicalCharacterAttribute)
        }
        logicHooks.onApplicationOfAttribute(logicalCharacter, logicalCharacterAttribute,
                stacksToApply)
        // TODO: Add animation showing this occurs
        // TODO:  Make animations more convenient
    }
}