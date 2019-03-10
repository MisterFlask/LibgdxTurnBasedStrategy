package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalUnitEffect
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.DamageOperator
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.EntitySpawner
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import org.reflections.Reflections
import javax.inject.Inject
import javax.inject.Singleton


public class AutoInjector(){

    fun instantiateAutoinjectables(){
        val reflections = Reflections("com.ironlordbyron")
        val annotated = reflections.getTypesAnnotatedWith(Autoinjectable::class.java)
        val eventNotifier = GameModuleInjector.moduleInjector.getInstance(EventNotifier::class.java)
        val effectRegistrar = GameModuleInjector.moduleInjector.getInstance(FunctionalEffectRegistrar::class.java)
        for (item in annotated){
            val instance = GameModuleInjector.moduleInjector.getInstance(item)
            if (instance is FunctionalUnitEffect<*>){
                effectRegistrar.registerAttribute(instance)
            }
            if (instance is EventListener){
                eventNotifier.registerGuiListener(instance)
            }
            if (instance is GameEventListener){
                eventNotifier.registerGameListener(instance)
            }
        }
    }
}

@Target(AnnotationTarget.CLASS)
annotation class Autoinjectable

public class AppliesAttributeOnHit(val entitySpawner: EntitySpawner,
                                   val logicHooks: LogicHooks) : FunctionalUnitEffect<AppliesAttributeOnHitLogicalEffect>{
    override val id: String = "APPLIES_ATTRIBUTE_ON_HIT"
    override val clazz = AppliesAttributeOnHitLogicalEffect::class.java

    override fun onStrikingEnemy(logicalAttr: AppliesAttributeOnHitLogicalEffect,
                                 thisCharacter: LogicalCharacter,
                                 struckCharacter: LogicalCharacter,
                                 logicalCharacterAttribute: LogicalCharacterAttribute) {
        // TODO: Add generic function for showing off the application of status effects to enemy
        // probably in the EntitySpawner (should create better name)
        println("Struck enemy effect applied!")


    }
}

public interface LogicalEffect{
    val id: String
    fun toPair(): Pair<String, LogicalEffect> {
        return id to this
    }
}

public data class AppliesAttributeOnHitLogicalEffect(val logicalAttributeApplied: LogicalCharacterAttribute) : LogicalEffect{
    override val id = "APPLIES_ATTRIBUTE_ON_HIT"
}

// TODO: Migrate this into a separate class
@Singleton
@Autoinjectable
public class FunctionalEffectRegistrar() {

    val functionalAttributes = ArrayList<FunctionalUnitEffect<*>>()
    fun registerAttribute(functionalUnitAttribute: FunctionalUnitEffect<*>){
        functionalAttributes.add(functionalUnitAttribute)
    }

    fun getAttributeById(id: String): FunctionalUnitEffect<*>{
        return functionalAttributes.first{it.id == id}
    }

    fun runEffectsOnCharacter(logicalCharacter: LogicalCharacter,
                              func: (FunctionalUnitEffect<*>, Any, LogicalCharacterAttribute) -> Unit){
        val attributes = logicalCharacter.attributes
        for (attr in attributes){

            for (attrKey in attr.customEffects.keys){
                val logicalAttributeParams = attr.customEffects[attrKey]
                val functionalAttribute = getAttributeById(attrKey)
                func(functionalAttribute, logicalAttributeParams!!, attr)
            }
        }
    }

    fun runTurnStartEffects(logicalCharacter: LogicalCharacter){
        runEffectsOnCharacter(logicalCharacter){
            funcAttr, logAttrParams, logicalCharacterAttribute ->
            val funAttr = funcAttr as FunctionalUnitEffect<Any>
            funAttr.onTurnStart(
                    logicalAttr = logAttrParams,
                    thisCharacter = logicalCharacter,
                    logicalCharacterAttribute = logicalCharacterAttribute)

        }
    }

    fun runDeathEffects(logicalCharacter: LogicalCharacter){
        runEffectsOnCharacter(logicalCharacter){
            funcAttr, logAttrParams,logicalCharacterAttribute->
            val funAttr = funcAttr as FunctionalUnitEffect<Any>
            funAttr.onDeath(
                        logicalAttr = logAttrParams,
                        thisCharacter = logicalCharacter,
                        logicalCharacterAttribute = logicalCharacterAttribute
                    )

        }
    }

    fun getMovementModifiers(logicalCharacter: LogicalCharacter) : Int{
        var movementModifierTotal = 0
        runEffectsOnCharacter(logicalCharacter){
            funcAttr, logAttrParams, logicalCharacterAttribute ->
            val funAttr = funcAttr as FunctionalUnitEffect<Any>
            val movementMod = funAttr.getMovementModifier(logAttrParams, logicalCharacter, logicalCharacterAttribute)
            movementModifierTotal += movementMod
        }
        return movementModifierTotal
    }
}