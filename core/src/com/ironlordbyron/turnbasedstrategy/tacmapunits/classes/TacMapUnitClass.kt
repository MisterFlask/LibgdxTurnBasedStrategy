package com.ironlordbyron.turnbasedstrategy.tacmapunits.classes

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.abilities.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.types.FunctionalEffectParameters
import com.ironlordbyron.turnbasedstrategy.common.equipment.EquipmentClass
import com.ironlordbyron.turnbasedstrategy.common.equipment.LogicalEquipment
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.entrypoints.FunctionalEffectRegistrar
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplate
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateSpawner
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method
import javax.inject.Singleton

///Defines the different kinds of unit players are allowed to have.
public abstract class TacMapUnitClass(val name: String,
                                      val description: String,
                                      val secondaryWeaponClass: EquipmentClass,
                                      val startingHp: Int,
                                      val startingArmor: Int,
                                      val startingMovement: Int,
                                      val startingSecondaryWeapon: LogicalEquipment,
                                      val startingMp: Int = 0,
                                      val protoActor: ProtoActor){

    fun createNewTacMapUnit(): TacMapUnitTemplate{
        return TacMapUnitTemplate(startingMovement,
                protoActor,
                name,
                listOf(),
                allowedEquipment = listOf(secondaryWeaponClass),
                walkableTerrainTypes = listOf(TerrainType.FOREST, TerrainType.GRASS),
                startingAttributes = listOf(),
                maxHealth = startingHp,
                equipment = arrayListOf(startingSecondaryWeapon)
            )
    }
}
@Singleton
@Autoinjectable
class TacMapUnitClassRegistrar(){
    val classesRegistered = ArrayList<UnitTemplateClassFactory>()


    fun registerClasses(){
        Logging.DebugGeneral("Registering unit classes!")

        val reflections = Reflections(ConfigurationBuilder()
                .addScanners(MethodAnnotationsScanner())
                .addUrls(ClasspathHelper.forPackage("com.ironlordbyron")))
        val annotated = reflections.getMethodsAnnotatedWith(InstantiableUnitClass::class.java)
        for (item in annotated){

            if (item.returnType.canonicalName != TacMapUnitClass::class.java.canonicalName){
                throw Exception("${item.name} should have a return type TacMapUnitClass, but does not")
            }
            if (item.parameterCount != 0){
                throw Exception("${item.name} should have no parameters.")
            }
            val annotation = item.getAnnotation(SpawnableUnitTemplate::class.java)
            val tacMapUnitTemplate = item.invoke(null)
            classesRegistered.add(UnitTemplateClassFactory(null, item, annotation.id))
        }

        Logging.DebugGeneral("Registered ${classesRegistered.size} unit templates!")
    }
}

annotation class InstantiableUnitClass
data class UnitTemplateClassFactory(val obj: Any?, val method: Method, val id: String){
    fun build() : TacMapUnitClass{
        return method.invoke(null) as TacMapUnitClass
    }
}

class SelfOnlyRangeStyle: RangeStyle{
    override fun getTargetableTiles(characterUsing: LogicalCharacter, logicalAbilityAndEquipment: LogicalAbilityAndEquipment, sourceSquare: TileLocation?): Collection<TileLocation> {
        return listOf(characterUsing.tileLocation)
    }

}