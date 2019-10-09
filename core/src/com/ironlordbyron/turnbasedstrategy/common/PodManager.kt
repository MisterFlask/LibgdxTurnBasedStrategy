package com.ironlordbyron.turnbasedstrategy.common

import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import java.util.*

public class PodManager{
    val tacMapState by LazyInject(TacticalMapState::class.java)
    val actionManager by LazyInject(ActionManager::class.java)
    fun awakenPod(podId: UUID){
        val unitsForPod = tacMapState.listOfCharacters.filter{it.tacMapUnit.podId == podId}
        val tiles = unitsForPod.map{it.tileLocation}

        actionManager.revealTilesTemporarily("POD_AWAKEN_REVEAL", tiles)
        for (unit in unitsForPod){
            actionManager.awakenUnit(unit)
        }
        actionManager.hideTiles( "POD_AWAKEN_REVEAL")
    }
}