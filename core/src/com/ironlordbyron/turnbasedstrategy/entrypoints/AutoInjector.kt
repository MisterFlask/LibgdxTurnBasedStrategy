package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.ironlordbyron.turnbasedstrategy.common.LogicHooks
import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalUnitEffect
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import org.reflections.Reflections
import java.lang.IllegalArgumentException
import javax.inject.Singleton


public class AutoInjector(){

    fun instantiateAutoinjectables(){
        val reflections = Reflections("com.ironlordbyron")
        val annotated = reflections.getTypesAnnotatedWith(Autoinjectable::class.java)
        val eventNotifier = GameModuleInjector.generateInstance(EventNotifier::class.java)
        val effectRegistrar = GameModuleInjector.generateInstance(FunctionalEffectRegistrar::class.java)
        for (item in annotated){
            val instance = GameModuleInjector.generateInstance(item)
            if (instance is FunctionalUnitEffect){
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
public class AppliesAttributeOnHit() : FunctionalUnitEffect() {
    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val logicHooks: LogicHooks by lazy{
        GameModuleInjector.generateInstance(LogicHooks::class.java)
    }

    override fun onStrikingEnemy(
                                 thisCharacter: LogicalCharacter,
                                 struckCharacter: LogicalCharacter,
                                 logicalCharacterAttribute: LogicalCharacterAttribute) {
        // TODO: Add generic function for showing off the application of status effects to enemy
        // probably in the ActionManager (should create better name)
        println("Struck enemy effect applied!")


    }
}

// TODO: Migrate this into a separate class
@Singleton
@Autoinjectable
public class FunctionalEffectRegistrar() {

    val functionalAttributes = ArrayList<FunctionalUnitEffect>()
    fun registerAttribute(functionalUnitAttribute: FunctionalUnitEffect){
        functionalAttributes.add(functionalUnitAttribute)
    }

    fun runEffectsOnCharacter(logicalCharacter: LogicalCharacter,
                              func: (FunctionalUnitEffect, LogicalCharacterAttribute) -> Unit){
        val attributes = logicalCharacter.attributes
        for (attr in attributes.toList()){
            for (effect in attr.customEffects){
                func(effect, attr)
            }
        }
    }

    fun runTurnStartEffects(logicalCharacter: LogicalCharacter){
        runEffectsOnCharacter(logicalCharacter){
            funcAttr, logicalCharacterAttribute ->
            funcAttr.onTurnStart(
                    thisCharacter = logicalCharacter,
                    logicalCharacterAttribute = logicalCharacterAttribute)

        }
    }

    fun runDeathEffects(logicalCharacter: LogicalCharacter){
        runEffectsOnCharacter(logicalCharacter){
            funcAttr,logicalCharacterAttribute->
            funcAttr.onDeath(
                        thisCharacter = logicalCharacter,
                        logicalCharacterAttribute = logicalCharacterAttribute
                    )

        }
    }

    fun getMovementModifiers(logicalCharacter: LogicalCharacter) : Int{
        var movementModifierTotal = 0
        runEffectsOnCharacter(logicalCharacter){
            funcAttr, logicalCharacterAttribute ->
            val movementMod = funcAttr.getMovementModifier(logicalCharacter, logicalCharacterAttribute)
            movementModifierTotal += movementMod
        }
        return movementModifierTotal
    }

    /**
     * Runs the effects that occur on application of the given logical character attribute.
     */
    fun runOnApplicationEffects(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){
        runEffectsOnCharacter(logicalCharacter){
            funcAttr, logicalCharacterAttribute ->
            funcAttr.afterApplication( logicalCharacter, logicalCharacterAttribute)
        }
    }

    fun canUnitAct(logicalCharacter: LogicalCharacter): Boolean {
        var stoppedFromActing = false
        runEffectsOnCharacter(logicalCharacter){
            funcAttr,  _ ->
                if (funcAttr.stopsUnitFromActing){
                    stoppedFromActing = true
                }
        }
        return !stoppedFromActing
    }

    fun runAfterStruckCharacterEffects(targetCharacter: LogicalCharacter) {
        runEffectsOnCharacter(targetCharacter){
            funcAttr, logicalCharacterAttribute ->
            funcAttr.onBeingStruck(targetCharacter, logicalCharacterAttribute)
        }
    }
}