package com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.ironlordbyron.turnbasedstrategy.ai.BasicAiDecisions
import com.ironlordbyron.turnbasedstrategy.ai.PathfinderFactory
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.characterattributes.LogicalCharacterAttribute
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.controller.MapHighlighter
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tacmapunits.tacMapState
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.BoundingBoxType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tileentity.CityTileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorCloneProtoEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.DoorEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileProtoEntity
import com.ironlordbyron.turnbasedstrategy.view.ActorName
import com.ironlordbyron.turnbasedstrategy.view.ActorOrdering
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimationSpeedManager
import com.ironlordbyron.turnbasedstrategy.view.animation.SpriteColorActorAction
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.*
import com.ironlordbyron.turnbasedstrategy.view.animation.camera.GameCameraProvider
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.external.SpecialEffectManager
import com.ironlordbyron.turnbasedstrategy.view.setFunctionalName
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
public class ActionManager @Inject constructor(
        private val characterImageManager: CharacterImageManager,
        private val boardState: TacticalMapState,
        private val eventNotifier: EventNotifier,
        private  val persistentActorGenerator: PersistentActorGenerator,
        private  val stageProvider: StageProvider,
        private val tileMapProvider: TileMapProvider,
        private  val movementAnimationGenerator: MovementAnimationGenerator,
        private  val revealActionGenerator: RevealActionGenerator,
        private   val logicalTileTracker: LogicalTileTracker,
        private   val actorSwapGenerator: ActorSwapAnimationGenerator,
        private    val tiledMapStageProvider: StageProvider,
        private   val animationActionQueueProvider: AnimationActionQueueProvider,
        private    val hideAnimationGenerator: HideAnimationGenerator,
        private    val visibleCharacterDataFactory: VisibleCharacterDataFactory,
        private  val specialEffectManager: SpecialEffectManager,
        private  val temporaryAnimationGenerator: TemporaryAnimationGenerator,
        private   val floatingTextGenerator: FloatingTextGenerator,
        private val basicAiDecisions: BasicAiDecisions

)  {


    val globalTacMapState by LazyInject(GlobalTacMapState::class.java)
    fun addCharacterToMapFromDeploymentZone(tacMapUnit: TacMapUnitTemplate,
                                            tileLocation: TileLocation){
        tacMapState.unitsAvailableToDeploy.removeAndAssert(tacMapUnit)
        addCharacterToTileFromTemplate(tacMapUnit, tileLocation, true)
        globalTacMapState.isMissionStarted = true
    }

    fun evacuateCharacter(logicalCharacter: LogicalCharacter){
        despawnEntityInSequence(logicalCharacter.actor)
        tacMapState.listOfCharacters.removeAndAssert(logicalCharacter)
        tacMapState.evacuatedUnits.add(logicalCharacter.tacMapUnit)
    }

    fun addCharacterToTileFromTemplate(tacMapUnit: TacMapUnitTemplate,
                                       tileLocation: TileLocation,
                                       playerControlled: Boolean,
                                       popup: String? = null) : LogicalCharacter {

        val tacMapUnitTemplate = tacMapUnit
        println("Adding character to tile: ${tacMapUnit.templateName} at ${tileLocation}")
        val group = characterImageManager.placeCharacterActor(tileLocation,tacMapUnitTemplate.protoActor)
        val characterSpawned = LogicalCharacter(group, tileLocation, tacMapUnitTemplate, playerControlled)
        visibleCharacterDataFactory.generateCharacterHpMarker(characterSpawned)
        boardState.addCharacter(characterSpawned)
        animationActionQueueProvider.addAction(revealActionGenerator.generateRevealActorActionPair(characterSpawned.actor))
        if (popup!=null){
            this.risingText(popup, tileLocation)
        }
        if (!characterSpawned.playerControlled){
            characterSpawned.formulateNewIntent()
        }
        characterSpawned.actor.setFunctionalName(ActorName(ActorOrdering.UNIT))
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


    val TIME_TO_MOVE = .5f
    //TODO: Migrate this to an animation generator
    private fun getCharacterMovementActorActionPair(toTile: TileLocation,
                                                    character: LogicalCharacter,
                                                    breadcrumbHint: List<TileLocation>? = null) : List<ActorActionPair> {
        val breadcrumbs = breadcrumbHint?:getBreadcrumbs(character, toTile)
        val actorActionPairs = ArrayList<ActorActionPair>()
        val timePerSquare = TIME_TO_MOVE/breadcrumbs.size
        for (breadcrumb in breadcrumbs){
            val libgdxLocation = logicalTileTracker.getLibgdxCoordinatesFromLocation(breadcrumb)
            var moveAction: Action = Actions.moveTo(libgdxLocation.x.toFloat(), libgdxLocation.y.toFloat(), timePerSquare / AnimationSpeedManager.animationSpeedScale)
            actorActionPairs.add(ActorActionPair(character.actor, moveAction))
        }
        return actorActionPairs
    }

    val pathfinderFactory: PathfinderFactory by LazyInject(PathfinderFactory::class.java)
    val pulseAnimationGenerator: PulseAnimationGenerator by LazyInject(PulseAnimationGenerator::class.java)
    val mapHighlighter: MapHighlighter by LazyInject(MapHighlighter::class.java)
    val logicHooks: LogicHooks by LazyInject(LogicHooks::class.java)

    private fun getBreadcrumbs(logicalCharacter: LogicalCharacter,
                               toTile: TileLocation): List<TileLocation> {
        val pathfinder = pathfinderFactory.createGridGraph(logicalCharacter)
        val tiles = pathfinder.acquireBestPathTo(
                logicalCharacter,
                toTile,
                allowEndingOnLastTile = true,
                allowFuzzyMatching = false)
        return tiles?.map{it.location}?.toList() ?: throw IllegalStateException("Required to call this on a character that can go to the provided tile")
    }


    // moves the character to the given tile logically, and returns the actor/action pair for animation purposes.
    fun moveCharacterToTile(character: LogicalCharacter, toTile: TileLocation, waitOnMoreQueuedActions: Boolean,
                            wasPlayerInitiated: Boolean){
        if (toTile == character.tileLocation){
            return
        }
        if (!wasPlayerInitiated) {
            // first, show the player where the ai COULD move to
            val tilesToHighlight = tacticalMapAlgorithms.getWhereCharacterCanMoveTo(character)
            val actorActionPairForHighlights = mapHighlighter.getTileHighlightActorActionPairs(tilesToHighlight, HighlightType.ENEMY_MOVE_TILE)
            val pulseActionPair = pulseAnimationGenerator.generateActorActionPair(character.actor.characterActor, 1f / AnimationSpeedManager.animationSpeedScale)
            actorActionPairForHighlights.secondaryActions += pulseActionPair
            animationActionQueueProvider.addAction(actorActionPairForHighlights)
        }

        val result = getCharacterMovementActorActionPair(toTile, character)
        boardState.moveCharacterToTile(character, toTile)
        animationActionQueueProvider.addActions(result)

        if (wasPlayerInitiated){
            logicHooks.playerMovedCharacter(character)
        }

        if (character.endedTurn){
            animationActionQueueProvider.addAction(SpriteColorActorAction.build(character, SpriteColorActorAction.DIM_COLOR))
        }
        if (!waitOnMoreQueuedActions){
            animationActionQueueProvider.runThroughActionQueue(finalAction = {})
            animationActionQueueProvider.clearQueue()
        }

        // now mark the character as moved by darkening the sprite.
    }


    fun openDoorAction(location: TileLocation) {
        if (!logicalTileTracker.isDoor(location)){
            throw IllegalArgumentException("Cannot call openDoorAction where there is no door, at tile $location")
        }
        val doorEntity = logicalTileTracker.getEntitiesAtTile(location).first{it is DoorEntity} as DoorEntity
        doorEntity.isOpen = true

        this.destroyTileEntity(doorEntity)
        this.createTileEntity(DoorCloneProtoEntity(doorEntity, true), location)
        //return actorSwapGenerator.generateActorSwapActorActionPair(
        //        doorEntity.openAnimation,
        //        AnimatedImageParams(startsVisible = false, loops = true),
        //        originalActor = doorEntity)
    }

    /**
     * Spawns an entity at a tile, AND puts it on the animation queue.
     */
    fun spawnActorAtTileInSequence(actor: Actor,
                                    tileLocation: TileLocation,
                                    boundingBoxType: BoundingBoxType = BoundingBoxType.WHOLE_TILE) : Actor{
        val boundingBox = tileMapProvider.getBoundingBoxOfTile(tileLocation, boundingBoxType)
        actor.setBoundingBox(boundingBox)
        tiledMapStageProvider.tiledMapStage.addActor(actor)
        actor.isVisible = false
        animationActionQueueProvider.addAction(ActorActionPair(actor, revealActionGenerator.generateRevealAction(actor)))
        return actor
    }
    /**
     * Spawns an entity at a tile, AND puts it on the animation queue.
     */
    fun spawnEntityAtTileInSequence(protoActor: ProtoActor,
                                    tileLocation: TileLocation,
                                    animatedImageParams: AnimatedImageParams = AnimatedImageParams.RUN_ALWAYS_AND_FOREVER,
                                    boundingBoxType: BoundingBoxType = BoundingBoxType.WHOLE_TILE,
                                    isChildActor: Boolean = false) : Actor{
        val actor = protoActor.toActorWrapper(animatedImageParams).actor
        return spawnActorAtTileInSequence(actor, tileLocation, boundingBoxType)
    }

    fun spawnAttributeActorAtTileInSequence(logicalAttribute: LogicalCharacterAttribute,
                                            logicalCharacter: LogicalCharacter,
                                            animatedImageParams: AnimatedImageParams =  AnimatedImageParams.RUN_ALWAYS_AND_FOREVER){
        if (logicalAttribute.tacticalMapProtoActor == null){
            return
        }
        val actor = logicalAttribute.tacticalMapProtoActor.toActorWrapper(animatedImageParams).actor
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

    // Note: cameraFocusActor is a hack around the fact that it's hard to distinguish "container" actors from the actual things users see,
    // sometimes resulting in artifacts where the camera zooms into 0,0.  If this happens, we can just use this.  HACK!
    fun despawnEntityInSequence(actor: Actor, cameraFocusActor: Actor? = null){
        animationActionQueueProvider.addAction(hideAnimationGenerator.generateHideActorActionPair(actor, cameraFocusActor))
    }

    fun destroySpecialEffectInSequence(uuid: UUID, motherActor: Actor){
        animationActionQueueProvider.addBareAction(motherActor, {specialEffectManager.destroyLineEffect(uuid)})
    }

    fun risingText(text: String, tileLocation: TileLocation, scale: Float = 1.0f){
        animationActionQueueProvider.addAction(
                floatingTextGenerator.getTemporaryAnimationActorActionPair("${text}", tileLocation, scale))
    }

    fun conquerCityAction(text:String, cityTileEntity: CityTileEntity){
        risingText(text, cityTileEntity.tileLocation)

        animationActionQueueProvider.addAction(
            customAction(cityTileEntity.actor){
                cityTileEntity.conquerByDemonAction()
        })
    }

    private fun customAction(actor: Actor, customAction: ()->Unit): ActorActionPair{
        return ActorActionPair(actor, AnimationActionQueueProvider.CustomAction(customAction))
    }

    val speechBubbleAnimation: SpeechBubbleAnimation by lazy {
        GameModuleInjector.generateInstance(SpeechBubbleAnimation::class.java)
    }

    /**
     * Defines action to be performed when user clicks on or hovers over a tile.
     */
    class ThingClickedListener(
                               val clicked: AtomicBoolean) : ClickListener() {
        override fun touchDown(event: InputEvent, x: Float,
                               y: Float, pointer: Int, button: Int): Boolean {
            clicked.set(true)
            return false
        }
    }

    fun runThroughActionQueue(){
        animationActionQueueProvider.runThroughActionQueue({})
    }
    fun createAwaitedSpeechBubbleForCharacter(text: String, tacMapUnit: TacMapUnitTemplate, afterUserClosesBubble: ()-> Unit = {}){

        val actor = speechBubbleAnimation.createTextBoxAtTopOfScreenWithCharacter(text,
                protoActor = tacMapUnit.protoActor)
        actor.isVisible = false
        val isDone = AtomicBoolean(false)
        actor.addListener(ThingClickedListener(isDone))
        actor.addHoverLighting()
        stageProvider.tacMapHudStage.addActor(actor)

        animationActionQueueProvider.addAction(
            ActorActionPair(actor,
                Actions.sequence(
                    Actions.alpha(0f),
                    Actions.visible(true),
                    Actions.fadeIn(.2f),
                    TriggeredDelayAction(isDone),
                    Actions.fadeOut(.2f),
                    Actions.removeActor(),
                    AnimationActionQueueProvider.CustomAction(afterUserClosesBubble)
                ),
                murderActorsOnceCompletedAnimation = true,
                cameraTrigger = false,
                startsVisible = false)
        )
    }

    fun createSpeechBubbleForCharacter(text: String,
                                       tacMapUnit: TacMapUnitTemplate,
                                       timeToLiveInSeconds: Float,
                                       //  todo: Figure out how to make wait-for-click work with animation engine
                                       waitForClick: Boolean = false){

        val actor = speechBubbleAnimation.createTextBoxAtTopOfScreenWithCharacter(text,
                protoActor = tacMapUnit.protoActor)
        actor.isVisible = false
        stageProvider.tacMapHudStage.addActor(actor)

        animationActionQueueProvider.addAction(
                ActorActionPair(actor,
                        Actions.sequence(
                                Actions.alpha(0f),
                                Actions.visible(true),
                                Actions.fadeIn(.2f),
                                Actions.delay(timeToLiveInSeconds),
                                Actions.fadeOut(.2f),
                                Actions.removeActor()
                        ),
                        murderActorsOnceCompletedAnimation = true,
                        cameraTrigger = false,
                        startsVisible = false)
        )
    }
    val cameraProvider by LazyInject(GameCameraProvider::class.java)
    fun createSpeechBubbleAtLocation(tileLocation: TileLocation, text: String){
        val actor = speechBubbleAnimation.createLocationOrientedTextBox(text, tileLocation = tileLocation)
        actor.isVisible = false
        actor.setTransform(true)
        actor.setScale(cameraProvider.camera.zoom)
        stageProvider.tiledMapStage.addActor(actor)

        animationActionQueueProvider.addAction(
                ActorActionPair(actor,
                        Actions.sequence(
                        Actions.alpha(0f),
                        Actions.visible(true),
                        Actions.fadeIn(.2f),
                        Actions.delay(1f),
                        Actions.fadeOut(.2f),
                        Actions.removeActor()
                ),
                murderActorsOnceCompletedAnimation = true,
                cameraTrigger = false,
                startsVisible = false)
        )

    }

    fun destroyTileEntity(entity: TileEntity) {
        logicalTileTracker.tileEntities.remove(entity)
        val tileLocationActor = entity.tileLocations.first().logicalTile()!!.actor // HACK: Camera focus actor necessary here; we're hiding the right actor, but technically its location is 0,0
        this.despawnEntityInSequence(entity.actor, cameraFocusActor = tileLocationActor)
    }

    fun createTileEntity(protoEntity: TileProtoEntity<*>, tileLocation: TileLocation){
        if (logicalTileTracker.tileEntities.any{it.tileLocations.contains(tileLocation)}){
            throw IllegalStateException("Entity already placed at tile location")
        }
        val entity = protoEntity.toTileEntity(tileLocation)
        logicalTileTracker.tileEntities.add(entity)
        this.spawnActorAtTileInSequence(entity.actor, tileLocation)
        entity.actor.setFunctionalName(ActorName(ActorOrdering.TILE_FEATURE))
        logicHooks.mapReorderRequired()
    }

}

class TriggeredDelayAction(val trigger: AtomicBoolean): Action() {
    override fun act(delta: Float): Boolean {
        return trigger.get()
    }

}

val DIM_COLOR = Color(.5f,.5f,.5f, 1f)
val BRIGHT_COLOR = Color(2f, 2f, 2f, 1f)
private class HoverGlowListener(val actor: Actor) : InputListener() {
    init{
        this.actor.color = DIM_COLOR
    }
    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        super.enter(event, x, y, pointer, fromActor)
        actor.color = BRIGHT_COLOR
    }

    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        super.exit(event, x, y, pointer, toActor)
        actor.color = DIM_COLOR
    }
}

fun Actor.addHoverLighting(){
    this.addListener(HoverGlowListener(this))
}

// TODO: Data driven character generation
data class SpawnCharacterAtTileParams(val tacMapUnit: TacMapUnitTemplate, val tileLocation: TileLocation, val protoActor: ProtoActor, val animatedImageParams: AnimatedImageParams = AnimatedImageParams.RUN_ALWAYS_AND_FOREVER)