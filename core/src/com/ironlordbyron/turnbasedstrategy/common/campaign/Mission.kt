package com.ironlordbyron.turnbasedstrategy.common.campaign

import com.ironlordbyron.turnbasedstrategy.Logging
import com.ironlordbyron.turnbasedstrategy.common.GlobalTacMapState
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.wrappers.BattleGoal
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.tacMapState
import com.ironlordbyron.turnbasedstrategy.entrypoints.*
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.randomElement
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import javax.inject.Singleton

@Singleton
public abstract class Mission{
    abstract fun flavorText(): String

    abstract fun name() : String

    abstract fun requiredBattleGoals(): Collection<BattleGoal>

    fun generateBattleGoals(): Collection<BattleGoal>{
        val required = requiredBattleGoals()
        return required
    }

    fun modifyMapToAccommodateBattleGoals(){

    }
}


public class Destroy

interface BattleGoalGenerator{
    fun generateBattleGoal() : BattleGoal
}

annotation class RegisteredBattleGoal

@Singleton
@Autoinjectable
public class BattleGoalGeneratorRegistrar(){
    val goalGenerators = ArrayList<BattleGoalGenerator>()

    init{
        generateRandomSelectionOfBattleGoals()
    }

    fun generateNewBattleGoal(): BattleGoal {
        if (goalGenerators.isEmpty()) {
            throw IllegalStateException("Attempting to access generateNewBattleGirl without initializing registrar!")
        }
        return goalGenerators.randomElement().generateBattleGoal()
    }


    fun generateRandomSelectionOfBattleGoals(){
        val reflections = Reflections(ConfigurationBuilder()
                .addScanners(MethodAnnotationsScanner())
                .addUrls(ClasspathHelper.forPackage("")))
        val annotated = reflections.getMethodsAnnotatedWith(RegisteredBattleGoal::class.java)
        for (item in annotated){

            if (item.returnType.canonicalName != BattleGoalGenerator::class.java.canonicalName){
                throw Exception("${item.name} should have a return type TacMapUnitTemplate, but does not")
            }
            if (item.parameterCount != 0){
                throw Exception("${item.name} should have no parameters.")
            }
            val battleGenerator = item.invoke(null) as BattleGoalGenerator
            goalGenerators.add(battleGenerator)
        }

        Logging.DebugGeneral("Registered ${goalGenerators.size} battle goal generators!")
    }
}
