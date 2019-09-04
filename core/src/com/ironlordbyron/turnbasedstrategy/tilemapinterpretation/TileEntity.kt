package com.ironlordbyron.turnbasedstrategy.tilemapinterpretation

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.common.abilities.LogicalAbility
import com.ironlordbyron.turnbasedstrategy.common.floodFill
import com.ironlordbyron.turnbasedstrategy.common.terrainProperties
import com.ironlordbyron.turnbasedstrategy.controller.EventNotifier
import com.ironlordbyron.turnbasedstrategy.entrypoints.Autoinjectable
import com.ironlordbyron.turnbasedstrategy.font.TextLabelGenerator
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.guice.eventNotifier
import com.ironlordbyron.turnbasedstrategy.tacmapunits.actionManager
import com.ironlordbyron.turnbasedstrategy.tileentity.CityTileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.ActorSettable
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.consolidateActors
import com.ironlordbyron.turnbasedstrategy.view.ui.addLabel
import javax.inject.Singleton

class PortalProtoEntity(val protoActor: ProtoActor = SuperimposedTilemaps.toDefaultProtoActor()) : TileProtoEntity<PortalEntity>{
    val name: String = "Portal"
    val eventNotifier = GameModuleInjector.generateInstance(EventNotifier::class.java)
    override fun toTileEntity(tileLocation: TileLocation): PortalEntity {
        return PortalEntity(tileLocation, protoActor.toActorWrapper().actor)
    }
}


interface TileProtoEntity <T: TileEntity> {
    fun toTileEntity(tileLocation: TileLocation) : T
}

interface TileEntity {
    val tileLocations: Collection<TileLocation>
    val actor: Actor
    val name: String // TODO
    fun targetableByAbility(ability: LogicalAbility): Boolean{
        return false
    }

    fun init(){

    }

    fun runTurn(){

    }

    fun runOnDeath(){

    }

    fun buildUiDisplay(parentTable: Table) {

    }

}

class WarpingInPortalTileProtoEntity() : TileProtoEntity<WarpingInPortalTileEntity>{
    val protoActor = SuperimposedTilemaps.elementalImageNumber("4")
    override fun toTileEntity(tileLocation: TileLocation): WarpingInPortalTileEntity {
        return WarpingInPortalTileEntity(listOf(tileLocation), protoActor.toActorWrapper())
    }
}

class WarpingInPortalTileEntity(override val tileLocations: Collection<TileLocation>,
                                override val actor: Actor,
                                override val name: String = "Warp Rift",
                                var turnsLeftUntilEntityCreated: Int = 3) : TileEntity{
    init{
        assert(tileLocations.size == 1)
    }
    override fun runTurn() {
        turnsLeftUntilEntityCreated --
        if (turnsLeftUntilEntityCreated == 0){
            actionManager.destroyTileEntity(this)
            actionManager.createTileEntity(PortalProtoEntity(), this.tileLocations.first())
        }
    }

    val textLabelGenerator: TextLabelGenerator by LazyInject(TextLabelGenerator::class.java)
    override fun buildUiDisplay(parentTable: Table) {
        if (turnsLeftUntilEntityCreated > 1){
            parentTable.addLabel("Warping in portal in $turnsLeftUntilEntityCreated turns.")
        }else{
            parentTable.addLabel("Warping in portal next turn.")
        }
        parentTable.row()
    }
}


interface TileEntityGenerator{
    val singleTilePerEntity: Boolean
        get() = false

    fun generateTileEntity(tileLocations: Collection<TileLocation>) : TileEntity
    fun applicableToTile(tileLocation: TileLocation) : Boolean


    fun getConsolidatedActorFromTilesWithProperty(tileLocations: Collection<TileLocation>, property: String): Group {
        val actors = ArrayList<Actor>()
        val townActorCells = tileLocations.map { it.terrainProperties() }
        for (cellProperties in townActorCells) {
            for (cell in cellProperties) {
                if (cell.properties.contains(property)) {
                    actors.add(cell.getActor())
                }
            }
        }
        val consolidatedActor = actors.consolidateActors()
        return consolidatedActor
    }
}
val tiledMapInterpreter: TiledMapInterpreter by LazyInject(TiledMapInterpreter::class.java)
@Singleton
@Autoinjectable
public class CityTileEntityGenerator() : TileEntityGenerator{
    override fun generateTileEntity(tileLocations: Collection<TileLocation>): TileEntity {
        val consolidatedActor = getConsolidatedActorFromTilesWithProperty(tileLocations, "town")
        return CityTileEntity("City name",
                tileLocations.first(),
                actor = consolidatedActor
                )
    }

    override fun applicableToTile(tileLocation: TileLocation): Boolean {
        return tileLocation.terrainProperties().any{it.properties.contains("town")}
    }
}

@Autoinjectable
@Singleton
public class WallTileEntityGenerator() : TileEntityGenerator{
    override val singleTilePerEntity: Boolean
        get() = true
    override fun generateTileEntity(tileLocations: Collection<TileLocation>): TileEntity {
        return WallEntity(eventNotifier,
                tileLocations.single(),
                this.getConsolidatedActorFromTilesWithProperty(tileLocations, "wall"))
    }

    override fun applicableToTile(tileLocation: TileLocation): Boolean {
        return tileLocation.terrainProperties().any{it.properties.contains("wall")}
    }
}


@Autoinjectable
@Singleton
public class DoorTileEntityGenerator() : TileEntityGenerator{
    override val singleTilePerEntity: Boolean
        get() = true

    override fun generateTileEntity(tileLocations: Collection<TileLocation>): TileEntity {
        val consolidatedActor = getConsolidatedActorFromTilesWithProperty(tileLocations, "closed_door")
        return DoorEntity(eventNotifier,
                tileLocations.single(),
                consolidatedActor
        )
    }

    override fun applicableToTile(tileLocation: TileLocation): Boolean {
        return tileLocation.terrainProperties().any{it.properties.contains("closed_door")}
    }
}

@Autoinjectable
@Singleton
public class FortressTileEntityGenerator() : TileEntityGenerator{
    override fun generateTileEntity(tileLocations: Collection<TileLocation>): TileEntity {
        val consolidatedActor = getConsolidatedActorFromTilesWithProperty(tileLocations, "castle")
        return FortressEntity(5,
                actor = consolidatedActor,
                tileLocations = tileLocations
        )
    }

    override fun applicableToTile(tileLocation: TileLocation): Boolean {
        return tileLocation.terrainProperties().any{it.properties.contains("castle")}
    }
}

@Autoinjectable
@Singleton
class TileEntityRegistrar(){
    val entities = ArrayList<TileEntity>()
    val generators = ArrayList<TileEntityGenerator>()
    fun registerEntity(tileLocation: TileLocation): TileEntity?{
        val alreadyTaken = entities.flatMap{it.tileLocations}
        if (tileLocation in alreadyTaken){
            return null
        }
        for (gen in generators){
            if (gen.applicableToTile(tileLocation)){
                if (gen.singleTilePerEntity){
                    val entity = gen.generateTileEntity(listOf(tileLocation))
                    entities.add(entity)
                    return entity
                }

                val locs = tileLocation.floodFill { gen.applicableToTile(it) }
                val entity = gen.generateTileEntity(locs)
                entities.add(entity)
                return entity
            }
        }
        return null
    }

    fun registerGenerator(generator: TileEntityGenerator){
        generators.add(generator)
    }
}

class PortalEntity(
                   val tileLocation: TileLocation,
                   override var actor: Actor,
                   override val name: String = "portal") : TileEntity{
    val eventNotifier: EventNotifier by LazyInject(EventNotifier::class.java)
    override val tileLocations: Collection<TileLocation>
        get() = listOf(tileLocation)
    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return false
    }

    companion object {
        val portalProtoActor: ProtoActor = SuperimposedTilemaps(tileSetNames = listOf("Door1"), textureId = "0")
    }
}



class FortressEntity(var durability: Int,
                     override val actor: Actor,
                     override val tileLocations: Collection<TileLocation>,
                     override val name: String = "Fortress") : TileEntity{
    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return false
    }

    fun conquer(){
        durability --
    }

    override fun buildUiDisplay(parentTable: Table) {
        parentTable.addLabel("Fortress")
        parentTable.addLabel("Turns until surrender: ${durability}")
    }
}

class DoorCloneProtoEntity(val toClone: DoorEntity,
                          val open: Boolean) : TileProtoEntity<DoorEntity>{
    override fun toTileEntity(tileLocation: TileLocation): DoorEntity {
        val protoActor = if (open) toClone.openAnimation else toClone.closedAnimation
        return DoorEntity(toClone.eventNotifier, tileLocation, protoActor.toActorWrapper().actor, "door", toClone.hp, toClone.openAnimation, toClone.closedAnimation, open)
    }

}

class DoorEntity(val eventNotifier: EventNotifier,
                 val tileLocation: TileLocation,
                 override var actor: Actor,
                 override val name: String = "door",
                 val hp: Int = 3,
                 val openAnimation: ProtoActor = openDoorProtoActor,
                 val closedAnimation: ProtoActor = closedDoorProtoActor,
                 var isOpen: Boolean = false) : TileEntity, ActorSettable {
    override val tileLocations: Collection<TileLocation>
        get() = listOf(tileLocation)
    override fun targetableByAbility(ability: LogicalAbility): Boolean {
        return true;
    }

    companion object {
        val openDoorProtoActor: ProtoActor = SuperimposedTilemaps(tileSetNames = listOf("Door1"), textureId = "0")
        val closedDoorProtoActor: ProtoActor = SuperimposedTilemaps(tileSetNames = listOf("Door0"), textureId = "0")
        val animatedImageParams = AnimatedImageParams(startsVisible = true)
    }

    override fun buildUiDisplay(parentTable: Table) {
        parentTable.addLabel("Door!")
    }
}