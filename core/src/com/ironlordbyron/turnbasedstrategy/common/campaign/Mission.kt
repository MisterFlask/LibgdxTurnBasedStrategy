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
}

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
                .addUrls(ClasspathHelper.forPackage("com.ironlordbyron")))
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

public class DestroyOrganBattleGoalGenerator() : BattleGoalGenerator{
    override fun generateBattleGoal(): BattleGoal {
        val unitTemplateRegistrar by LazyInject(UnitTemplateRegistrar::class.java)
        val legitimateOrgans = unitTemplateRegistrar.unitTemplates
                .filter{it.compileTimeTags.contains(SpawnableUnitTemplateTags.ORGAN)}
        if (legitimateOrgans.isEmpty()){
            throw java.lang.IllegalStateException("No organs available for spawning!")
        }
        return DestroyOrganBattleGoal(legitimateOrgans.randomElement().id)
    }

}

@RegisteredBattleGoal
public fun destroyOrganBattleGoalGen() : BattleGoalGenerator{
    return DestroyOrganBattleGoalGenerator()
}

@RegisteredBattleGoal
public fun destroyNumberOfUnitsBattleGoalGen(): BattleGoalGenerator {
    return DestroyNumberOfUnitsBattleGoalGenerator()
}

class DestroyNumberOfUnitsBattleGoalGenerator() : BattleGoalGenerator{
    override fun generateBattleGoal(): BattleGoal {
        return DestroyNumberOfUnitsBattleGoal(5)
    }

}


// todo: gussy this up
public class DestroyOrganBattleGoal(val unitTemplateIdToDestroy: String,
                                            override val name: String = "Destroy $unitTemplateIdToDestroy",
                                            override val description: String = "Before the end of combat, destroy $unitTemplateIdToDestroy") : BattleGoal{
    val tacMapState by LazyInject(TacticalMapState::class.java)
        override fun isGoalMet(): Boolean {
            return tacMapState.deadCharacters.firstOrNull{it.templateId == unitTemplateIdToDestroy} != null
    }

}

public class DestroyNumberOfUnitsBattleGoal(val numToDestroy: Int,
                                            override val name: String = "Kill Demons",
                                            override val description: String = "before the end of combat, kill $numToDestroy non-minion enemies.") : BattleGoal{
    override fun isGoalMet(): Boolean {
        return nonMinionsDead() >= numToDestroy
    }

    fun nonMinionsDead() : Int {
        return tacMapState.deadCharacters.filter{it.nonMinionEnemy}.count()
    }

    override fun getGoalProgressString(): String {
        if (!isGoalMet()){
            return "${nonMinionsDead()} banished so far"
        }else{
            return super.getGoalProgressString()
        }

    }

}