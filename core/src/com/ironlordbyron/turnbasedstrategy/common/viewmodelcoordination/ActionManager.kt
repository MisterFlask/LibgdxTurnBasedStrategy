package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.tiledutils.CharacterImageManager
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.BoundingBoxType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tiledutils.setBoundingBox
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.LogicalCharacterActorGroup
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.*
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
public class ActionManager @Inject constructor(
        val characterImageManager: CharacterImageManager,
        val boardState: TacticalMapState,
        val eventNotifier: EventNotifier,
        val persistentActorGenerator: PersistentActorGenerator,
        val stageProvider: TacticalTiledMapStageProvider,
        val tileMapProvider: TileMapProvider,
        val movementAnimationGenerator: MovementAnimationGenerator,
        val revealActionGenerator: RevealActionGenerator,
        val logicalTileTracker: LogicalTileTracker,
        val actorSwapGenerator: ActorSwapAnimationGenerator,
        val tiledMapStageProvider: TacticalTiledMapStageProvider,
        val animationActionQueueProvider: AnimationActionQueueProvider,
        val hideAnimationGenerator: HideAnimationGenerator,
        val visibleCharacterDataFactory: VisibleCharacterDataFactory,
        val specialEffectManager: SpecialEffectManager,
        val temporaryAnimationGenerator: TemporaryAnimationGenerator,
        val floatingTextGenerator: FloatingTextGenerator
)  {

    fun addCharacterToTileFromTemplate(tacMapUnit: TacMapUnitTemplate, tileLocation: TileLocation, playerControlled: Boolean) : LogicalCharacter {

        val tacMapUnitTemplate = tacMapUnit.copy()
        println("Adding character to tile: ${tacMapUnit.templateName} at ${tileLocation}")
        val group = characterImageManager.placeCharacterActor(tileLocation,tacMapUnitTemplate.tiledTexturePath)
        val characterSpawned = LogicalCharacter(group, tileLocation, tacMapUnitTemplate, playerControlled)
        visibleCharacterDataFactory.generateCharacterHpMarker(characterSpawned)
        boardState.addCharacter(characterSpawned)
        animationActionQueueProvider.addAction(revealActionGenerator.generateRevealActorActionPair(characterSpawned.actor))
        return characterSpawned
    }

    // todo: Migrate to more appropriate location
    // todo: improve flexibility (really?  Only allowing modification of alphaOverride?)
    @Deprecated("Use spawnEntityAtTileInSequence instead")
    fun addActorToTile(tileLocation: TileLocation, protoActor: ProtoActor, alphaOverride: Float = 1f) : Actor {
        val actor = persistentActorGenerator.createPersistentActor(protoActor, alphaOverride = alphaOverride).actor
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation)
        actor.setBoundingBox(boundingBox)
        stageProvider.tiledMapStage.addActor(actor)
        return actor
    }

    fun generateLightTileOnFireAction(tileLocation: TileLocation) : ActorActionPair {
        val actor = addActorToTile(tileLocation, DataDrivenOnePageAnimation.FIRE, alphaOverride = .8f)
        return ActorActionPair(actor, revealActionGenerator.generateRevealAction(actor))
    }

    fun animateProjectileForLogicalAbility(logicalAbilityAndEquipment: LogicalAbilityAndEquipment,
                                           fromTile: TileLocation,
                                           toTile: TileLocation) : ActorActionPair? {
        val abilityProjectileProtoActor = logicalAbilityAndEquipment.ability.projectileActor
        if (abilityProjectileProtoActor == null){
            return null
        }
        val projectileActor = addActorToTile(fromTile, abilityProjectileProtoActor)
        return movementAnimationGenerator.createMoveActorToTileActorActionPair(toTile, projectileActor, true)
    }

    fun openDoorAction(location: TileLocation): ActorActionPair {
        if (!logicalTileTracker.isDoor(location)){
            throw IllegalArgumentException("Cannot call openDoorAction where there is no door, at tile $location")
        }
        val doorEntity = logicalTileTracker.getEntitiesAtTile(location).first{it is DoorEntity} as DoorEntity
        doorEntity.isOpen = true
        return actorSwapGenerator.generateActorSwapActorActionPair(doorEntity.openAnimation, AnimatedImageParams(startsVisible = false, loops = true), actorSettable = doorEntity)
    }

    /**
     * Spawns an entity at a tile, AND puts it on the animation queue.
     */
    fun spawnEntityAtTileInSequence(protoActor: ProtoActor,
                                    tileLocation: TileLocation,
                                    animatedImageParams: AnimatedImageParams = AnimatedImageParams.RUN_ALWAYS_AND_FOREVER,
                                    boundingBoxType: BoundingBoxType = BoundingBoxType.WHOLE_TILE,
                                    isChildActor: Boolean = false) : Actor{
        val actor = protoActor.toActor(animatedImageParams).actor
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation, boundingBoxType)
        actor.setBoundingBox(boundingBox)
        tiledMapStageProvider.tiledMapStage.addActor(actor)
        actor.isVisible = false
        animationActionQueueProvider.addAction(ActorActionPair(actor, revealActionGenerator.generateRevealAction(actor)))
        return actor
    }

    fun spawnAttributeActorAtTileInSequence(logicalAttribute: LogicalCharacterAttribute,
                                            logicalCharacter: LogicalCharacter,
                                            animatedImageParams: AnimatedImageParams =  AnimatedImageParams.RUN_ALWAYS_AND_FOREVER){
        if (logicalAttribute.tacticalMapProtoActor == null){
            return
        }
        val actor = logicalAttribute.tacticalMapProtoActor.toActor(animatedImageParams).actor
        actor.isVisible = false
        animationActionQueueProvider.addAction(ActorActionPair(actor, revealActionGenerator.generateRevealAction(actor)))
        val actorGroup = logicalCharacter.actor
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(logicalCharacter.tileLocation)
                .copy(x = logicalAttribute.tacticalMapProtoActorOffsetX,
                        y = logicalAttribute.tacticalMapProtoActorOffsetY)
        actor.setBoundingBox(boundingBox)
        actorGroup.addActor(actor)
        if (actorGroup.attributeActors[logicalAttribute.id] != null){
            throw Exception("Oh no!  Attempted to add attribute actor to same character >1 time: ${logicalAttribute.id}")
        }
        actorGroup.attributeActors[logicalAttribute.id] = actor
    }

    fun despawnAttributeActorAtTileInSequence(logicalAttribute: LogicalCharacterAttribute,
                                              logicalCharacter: LogicalCharacter){
        val attrActor = logicalCharacter.actor.attributeActors[logicalAttribute.id]
        if (attrActor == null){
            throw Exception("Oh no!  Attempted to remove nonexistent attribute actor ${logicalAttribute.id}")
        }
        despawnEntityInSequence(attrActor)
    }

    data class SpawnEntityParams(val protoActor: ProtoActor,
                                 val tileLocation: TileLocation,
                                 val animatedImageParams: AnimatedImageParams)

    fun spawnEntitiesAtTilesInSequenceForTempAnimation(spawnEntityParams: Collection<SpawnEntityParams>){
        if (spawnEntityParams.isEmpty()){
            return
        }
        val first = convertToTempAnimationActorActionPairs(spawnEntityParams.first())
        val rest = ArrayList(spawnEntityParams)
        rest.removeAt(0)

        for (entity in rest){
            val next = convertToTempAnimationActorActionPairs(entity)
            first.secondaryActions.add(next)
        }
        first.name = "SpawnEntityFromParams"
        animationActionQueueProvider.addAction(first)
    }

    private fun convertToTempAnimationActorActionPairs(spawnEntityParams: SpawnEntityParams): ActorActionPair {
        return temporaryAnimationGenerator.getTemporaryAnimationActorActionPair(spawnEntityParams.tileLocation,
                spawnEntityParams.protoActor)
    }

    fun despawnEntityInSequence(actor: Actor){
        animationActionQueueProvider.addAction(hideAnimationGenerator.generateHideActorActionPair(actor))
    }

    fun destroySpecialEffectInSequence(uuid: UUID, motherActor: Actor){
        animationActionQueueProvider.addBareAction(motherActor, {specialEffectManager.destroyLineEffect(uuid)})
    }

    fun risingText(text: String, tileLocation: TileLocation){
        animationActionQueueProvider.addAction(
                floatingTextGenerator.getTemporaryAnimationActorActionPair("${text}", tileLocation))
    }




}
// TODO: Data driven character generation
data class SpawnCharacterAtTileParams(val tacMapUnit: TacMapUnitTemplate, val tileLocation: TileLocation, val protoActor: ProtoActor, val animatedImageParams: AnimatedImageParams = AnimatedImageParams.RUN_ALWAYS_AND_FOREVER)