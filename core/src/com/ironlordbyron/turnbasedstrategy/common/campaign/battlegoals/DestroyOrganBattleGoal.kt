package com.ironlordbyron.turnbasedstrategy.common.campaign.battlegoals

import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.campaign.BattleGoalGenerator
import com.ironlordbyron.turnbasedstrategy.common.campaign.RegisteredBattleGoal
import com.ironlordbyron.turnbasedstrategy.common.wrappers.BattleGoal
import com.ironlordbyron.turnbasedstrategy.common.wrappers.ZoneGenerationParameters
import com.ironlordbyron.turnbasedstrategy.entrypoints.SpawnableUnitTemplateTags
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.missiongen.LevelAppropriateMinionGenerator
import com.ironlordbyron.turnbasedstrategy.missiongen.UnitSpawnParameter
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.randomElement


public class DestroyOrganBattleGoalGenerator() : BattleGoalGenerator {
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
public fun destroyOrganBattleGoalGen() : BattleGoalGenerator {
    return DestroyOrganBattleGoalGenerator()
}

val levelAppropriateMinionGenerator by LazyInject(LevelAppropriateMinionGenerator::class.java)

public class DestroyMasterOrganWithShieldBattleGoal(): DestroyOrganBattleGoal(){
    override fun getRequiredZoneCreationParameters(): Collection<ZoneGenerationParameters> {
        val masterZone = ZoneGenerationParameters(unitSpawnParams = listOf(
                TacMapUnitTemplate.fromId("MASTER_ORGAN")
        ) + levelAppropriateMinionGenerator.getGenericMinions(3))
        val shieldZone = ZoneGenerationParameters(unitSpawnParams =
                listOf(TacMapUnitTemplate.fromId("SHIELDING_ORGAN")) + levelAppropriateMinionGenerator.getGenericMinions(3)
        )
        return listOf(masterZone, shieldZone)
    }
}


public open class DestroyOrganBattleGoal(val unitTemplateIdToDestroy: String = "MASTER_ORGAN",
                                         override val name: String = "Destroy $unitTemplateIdToDestroy",
                                         override val description: String = "Before the end of combat, destroy $unitTemplateIdToDestroy") : BattleGoal{
    val tacMapState by LazyInject(TacticalMapState::class.java)
    override fun isGoalMet(): Boolean {
        return tacMapState.deadCharacters.firstOrNull{it.templateId == unitTemplateIdToDestroy} != null
    }

    override fun getRequiredZoneCreationParameters(): Collection<ZoneGenerationParameters> {
        val minions = levelAppropriateMinionGenerator.getGenericMinions(3)
        return listOf(ZoneGenerationParameters(unitSpawnParams = listOf(
                 TacMapUnitTemplate.fromId(unitTemplateIdToDestroy)
        ) + minions))
    }
}

val unitTemplateRegistrar by LazyInject(UnitTemplateRegistrar::class.java)
public fun TacMapUnitTemplate.TacMapUnit.fromId(unitTemplateId: String): TacMapUnitTemplate {
    return unitTemplateRegistrar.getTacMapUnitById(unitTemplateId) ?: throw IllegalArgumentException("Cannot find unit template ID of $unitTemplateId")
}

