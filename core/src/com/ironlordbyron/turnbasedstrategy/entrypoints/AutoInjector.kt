package com.ironlordbyron.turnbasedstrategy.entrypoints

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalAttributeEffect
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.controller.EventListener
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.GameEventListener
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method
import javax.inject.Singleton


public class AutoInjector(){

    fun instantiateAutoinjectables(){
        val reflections = Reflections("com.ironlordbyron")
        val annotated = reflections.getTypesAnnotatedWith(Autoinjectable::class.java)
        val eventNotifier = GameModuleInjector.generateInstance(EventNotifier::class.java)
        val effectRegistrar = GameModuleInjector.generateInstance(FunctionalEffectRegistrar::class.java)
        val cadenceEffectRegistrar = GameModuleInjector.generateInstance(CadenceEffectsRegistrar::class.java)
        for (item in annotated){
            val instance = GameModuleInjector.generateInstance(item)
            if (instance is EventListener){
                eventNotifier.registerGuiListener(instance)
            }
            if (instance is GameEventListener){
                eventNotifier.registerGameListener(instance)
            }
            if (instance is TurnStartListener){
                cadenceEffectRegistrar.turnStartEffects.add(instance)
            }
            if (instance is BattleStartListener){
                cadenceEffectRegistrar.battleStartEffects.add(instance)
            }
        }
    }
}

@Target(AnnotationTarget.CLASS)
annotation class Autoinjectable
public class AppliesAttributeOnHit() : FunctionalAttributeEffect() {
    val actionManager: ActionManager by lazy{
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val logicHooks: LogicHooks by lazy{
        GameModuleInjector.generateInstance(LogicHooks::class.java)
    }

    override fun onStrikingEnemy(
                                 params: FunctionalEffectParameters) {
        // TODO: Add generic function for showing off the application of status effects to enemy
        // probably in the ActionManager (should create better name)
        println("Struck enemy effect applied!")


    }
}

annotation class SpawnableUnitTemplate(val id: String)

data class UnitTemplateSpawner(val obj: Any?, val method: Method, val id: String){
    fun spawn() : TacMapUnitTemplate{
        return method.invoke(null) as TacMapUnitTemplate
    }
}

@Singleton
@Autoinjectable
public class UnitTemplateRegistrar(){
    val unitTemplates = ArrayList<UnitTemplateSpawner>()

    fun getTacMapUnitById(id: String): TacMapUnitTemplate? {
        return unitTemplates.find{it.id == id}?.spawn()
    }

    init{
        registerUnitTemplates()
    }

    fun registerUnitTemplates(){
        Logging.DebugGeneral("Registering unit templates!")

        val reflections = Reflections(ConfigurationBuilder()
                .addScanners(MethodAnnotationsScanner())
                .addUrls(ClasspathHelper.forPackage("com.ironlordbyron")))
        val annotated = reflections.getMethodsAnnotatedWith(SpawnableUnitTemplate::class.java)
        val eventNotifier = GameModuleInjector.generateInstance(EventNotifier::class.java)
        val effectRegistrar = GameModuleInjector.generateInstance(FunctionalEffectRegistrar::class.java)
        for (item in annotated){

            if (item.returnType.canonicalName != TacMapUnitTemplate::class.java.canonicalName){
                throw Exception("${item.name} should have a return type TacMapUnitTemplate, but does not")
            }
            if (item.parameterCount != 0){
                throw Exception("${item.name} should have no parameters.")
            }
            val annotation = item.getAnnotation(SpawnableUnitTemplate::class.java)
            val tacMapUnitTemplate = item.invoke(null)
            unitTemplates.add(UnitTemplateSpawner(null, item, annotation.id))
        }

        Logging.DebugGeneral("Registered ${unitTemplates.size} unit templates!")
    }
}

@Singleton
@Autoinjectable
public class CadenceEffectsRegistrar(){
    val turnStartEffects: ArrayList<TurnStartListener> = arrayListOf()
    val battleStartEffects: ArrayList<BattleStartListener> = arrayListOf()


}


// TODO: Migrate this into a separate class
@Singleton
@Autoinjectable
public class FunctionalEffectRegistrar() {

    fun runTurnStartEffects(logicalCharacter: LogicalCharacter){
        val attributes = logicalCharacter.getAttributes()
        for (attr in attributes){
            for (effect in attr.logicalAttribute.customEffects){
                effect.onTurnStart(FunctionalEffectParameters(logicalCharacter, attr.logicalAttribute, attr.stacks))
            }
        }
    }

    fun runDeathEffects(logicalCharacter: LogicalCharacter){
        val attributes = logicalCharacter.getAttributes()
        for (attr in attributes){
            for (effect in attr.logicalAttribute.customEffects){
                effect.onDeath(FunctionalEffectParameters(logicalCharacter, attr.logicalAttribute, attr.stacks))
            }
        }
    }

    fun getMovementModifiers(logicalCharacter: LogicalCharacter) : Int{
        val attributes = logicalCharacter.getAttributes()
        var movemod = 0
        for (attr in attributes){
            for (effect in attr.logicalAttribute.customEffects){
                movemod+=effect.getMovementModifier(FunctionalEffectParameters(logicalCharacter, attr.logicalAttribute, attr.stacks))
            }
        }
        return movemod
    }

    /**
     * Runs the effects that occur on application of the given logical character attribute.
     */
    fun runOnApplicationEffects(logicalCharacter: LogicalCharacter, logicalCharacterAttribute: LogicalCharacterAttribute){
        val attributes = logicalCharacter.getAttributes()
        for (attr in attributes){
            for (effect in attr.logicalAttribute.customEffects){
                effect.afterApplication(FunctionalEffectParameters(logicalCharacter, attr.logicalAttribute, attr.stacks))
            }
        }
    }

    fun canUnitAct(logicalCharacter: LogicalCharacter): Boolean {
        val attributes = logicalCharacter.getAttributes()
        var canUnitAct = true
        for (attr in attributes){
            for (effect in attr.logicalAttribute.customEffects){
                canUnitAct = canUnitAct && !effect.stopsUnitFromActing
            }
        }
        return canUnitAct
    }

    fun runAfterStruckCharacterEffects(targetCharacter: LogicalCharacter) {
        val attributes = targetCharacter.getAttributes()
        for (attr in attributes){
            for (effect in attr.logicalAttribute.customEffects){
                effect.onBeingStruck(FunctionalEffectParameters(targetCharacter, attr.logicalAttribute, attr.stacks))
            }
        }
    }
}