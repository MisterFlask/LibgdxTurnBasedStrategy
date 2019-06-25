package com.ironlordbyron.turnbasedstrategy.tacmapunits.util

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.entrypoints.FunctionalEffectRegistrar
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateSpawner
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import javax.inject.Singleton


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
        for (item in annotated){

            if (item.returnType.canonicalName != TacMapUnitTemplate::class.java.canonicalName){
                throw Exception("${item.name} should have a return type TacMapUnitTemplate, but does not")
            }
            if (item.parameterCount != 0){
                throw Exception("${item.name} should have no parameters.")
            }
            val annotation = item.getAnnotation(SpawnableUnitTemplate::class.java)
            item.invoke(null)
            if (unitTemplates.any{it.id == annotation.id}){
                throw IllegalStateException("Uncovered multiple methods annotated with template ID ${annotation.id}")
            }
            unitTemplates.add(UnitTemplateSpawner(null, item, annotation.id))
        }

        Logging.DebugGeneral("Registered ${unitTemplates.size} unit templates!")
    }
}