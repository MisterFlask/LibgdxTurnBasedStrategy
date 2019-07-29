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
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.guice.LazyInject
import com.ironlordbyron.turnbasedstrategy.tileentity.CityTileEntity
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.ActorSettable
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.SuperimposedTilemaps
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.consolidateActors
import com.ironlordbyron.turnbasedstrategy.view.ui.addLabel
import javax.inject.Singleton

class PortalProtoEntity(val tileLocation: TileLocation, val actor: ProtoActor) : TileProtoEntity<PortalEntity>{
    val name: String = "Portal"
    val eventNotifier = GameModuleInjector.generateInstance(EventNotifier::class.java)
    override fun toTileEntity(): PortalEntity {
        return PortalEntity(eventNotifier, tileLocation, actor.toActor().actor)
    }

}

interface TileProtoEntity <T> {
    fun toTileEntity() : T
}

interface TileEntity {
    val tileLocations: Collection<TileLocation>
    val actor: Actor
    val name: String
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

interface TileEntityGenerator{
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

class PortalEntity(val eventNotifier: EventNotifier,
                   val tileLocation: TileLocation,
                   override var actor: Actor,
                   override val name: String = "portal") : TileEntity{
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
}