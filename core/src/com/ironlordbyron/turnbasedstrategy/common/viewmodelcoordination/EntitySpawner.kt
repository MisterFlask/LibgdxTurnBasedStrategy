package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.ironlordbyron.turnbasedstrategy.common.LogicalCharacter
import com.ironlordbyron.turnbasedstrategy.common.TacMapUnitTemplate
import com.ironlordbyron.turnbasedstrategy.common.TacticalMapState
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.TacticalGameEvent
import com.ironlordbyron.turnbasedstrategy.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.setBoundingBox
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.PersistentActorGenerator
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import javax.inject.Inject


public class EntitySpawner @Inject constructor(
        val characterImageManager: CharacterImageManager,
        val boardState: TacticalMapState,
        val eventNotifier: EventNotifier,
        val persistentActorGenerator: PersistentActorGenerator,
        val stageProvider: TacticalTiledMapStageProvider,
        val tileMapProvider: TileMapProvider
){
    fun addCharacterToTile(tacMapUnit: TacMapUnitTemplate, tileLocation: TileLocation, playerControlled: Boolean) {
        val actor = characterImageManager.placeCharacterActor(tileLocation,tacMapUnit.tiledTexturePath)
        val characterSpawned = LogicalCharacter(actor, tileLocation, tacMapUnit, playerControlled)
        boardState.listOfCharacters.add(characterSpawned)
        eventNotifier.notifyListenersOfGameEvent(TacticalGameEvent.UnitSpawned(characterSpawned))
    }

    fun addFireToTile(tileLocation: TileLocation){
        addActorToTile(tileLocation, DataDrivenOnePageAnimation.FIRE, alphaOverride = .8f)
    }

    fun addActorToTile(tileLocation: TileLocation, protoActor: ProtoActor, alphaOverride: Float = 1f){
        val actor = persistentActorGenerator.createPersistentActor(protoActor, alphaOverride = alphaOverride)
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        actor.setBoundingBox(boundingBox)
        stageProvider.tiledMapStage.addActor(actor)
    }
    fun animateProjectileCulminatingInActorCreation(projectileProtoActor : ProtoActor,
                                                    arrivalProtoActor: ProtoActor,
                                                    speed: Float, fromTile: TileLocation, toTile: TileLocation){
        val projectileActor = addActorToTile(fromTile, projectileProtoActor)
    }

}