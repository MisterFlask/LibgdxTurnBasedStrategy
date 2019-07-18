package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.AttributeOperator
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.entrypoints.UnitTemplateRegistrar
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.randomElement
import java.util.*
import javax.inject.Singleton


@Autoinjectable
@Singleton
public class GlobalTacMapState: TurnStartListener, BattleStartListener {

    val eventNotifierObject: EventNotifier by lazy {
        GameModuleInjector.generateInstance(EventNotifier::class.java)
    }

    var alertness = 0
    var alertnessValuesTriggered = ArrayList<Int>()
    var battleEventsToTrigger = HashMap<Int, BattleEvent>()

    init{
        battleEventsToTrigger.putAll(
                mapOf(
                    3 to ReinforcementsEvent("WEAK_SLIME"),
                    4 to ReinforcementsEvent("WEAK_SLIME"),
                    5 to MassStatusAfflictionEvent(LogicalCharacterAttribute.SLIMED)
                )

        )
    }


    override fun handleBattleStartEvent() {
        alertness = 0
    }

    override fun handleTurnStartEvent() {
        alertness ++
        for (event in battleEventsToTrigger){
            if (event.key <= alertness && !event.value.used){
                event.value.handle()
                event.value.used = true
            }
        }
    }

    fun nextEvent(): EventDescriptor {
        return battleEventsToTrigger
                .filter{!it.value.used}
                .minBy{it.key}
                .convertToEventDescriptor()
    }
}

private fun  Map.Entry<Int, BattleEvent>?.convertToEventDescriptor(): EventDescriptor {
    if (this == null){
        return EventDescriptor("None", -1)
    }
    return EventDescriptor(this.value.name, this.key)
}

data class EventDescriptor(val eventName: String, val atAlertness: Int)

class ReinforcementsEvent(val unitTemplateId: String) : BattleEvent(){
    override val name: String
        get() = "Reinforcements arrive!"

    val unitTemplateRegistrar: UnitTemplateRegistrar by lazy {
        GameModuleInjector.generateInstance(UnitTemplateRegistrar::class.java)
    }
    val actionManager: ActionManager by lazy {
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val tiledMapProvider: TileMapProvider by lazy {
        GameModuleInjector.generateInstance(TileMapProvider::class.java)
    }

    override fun handle() {
        val appropriateLocation = getSpawnableUnitLocation()
        val unit = unitTemplateRegistrar.getTacMapUnitById(unitTemplateId)!!
        actionManager.addCharacterToTileFromTemplate(unit, appropriateLocation, false)
    }

    private fun getSpawnableUnitLocation(): TileLocation {
        val tiles = tiledMapProvider.getSpawnableTilemapTiles()
        return tiles.randomElement()
    }

}

class MassStatusAfflictionEvent(val attribute: LogicalCharacterAttribute,
                                val numStacks: Int = 1) : BattleEvent(){
    val tacMapState: TacticalMapState by lazy {
        GameModuleInjector.generateInstance(TacticalMapState::class.java)
    }
    val actionManager: ActionManager by lazy {
        GameModuleInjector.generateInstance(ActionManager::class.java)
    }
    val attributeOperator: AttributeOperator by lazy {
        GameModuleInjector.generateInstance(AttributeOperator::class.java)
    }

    override fun handle() {
        tacMapState.listOfPlayerCharacters.forEach{
            attributeOperator.applyAttribute(it, attribute, numStacks)
        }
    }

    override val name: String = "Mass Status Affliction"
}

abstract class BattleEvent{
    abstract fun handle()
    abstract val name: String
    var used: Boolean = false
}

interface TurnStartListener {
    fun handleTurnStartEvent()
}
interface BattleStartListener{
    fun handleBattleStartEvent()
}
