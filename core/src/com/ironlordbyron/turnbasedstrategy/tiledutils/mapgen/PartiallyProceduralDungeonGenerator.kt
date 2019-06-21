package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.google.inject.Singleton
import com.ironlordbyron.turnbasedstrategy.common.*
import com.ironlordbyron.turnbasedstrategy.common.viewmodelcoordination.ActionManager
import com.ironlordbyron.turnbasedstrategy.tacmapunits.MasterOrgan
import com.ironlordbyron.turnbasedstrategy.tacmapunits.WeakMinionSpawner
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter
import java.util.*
import javax.inject.Inject

class RoomAdjacency(_room1: UUID,_room2: UUID, val walls: HashSet<TileLocation> = HashSet()){
    val room1: UUID
    val room2: UUID
    init{
        if (_room1 > _room2){
            room1 = _room1
            room2 = _room2
        } else{
            room1 = _room2
            room2 = _room1
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is RoomAdjacency){
            return room1 == other.room1 && room2 == other.room2
        }
        return false
    }

    override fun hashCode(): Int {
        return room1.hashCode() + room2.hashCode()
    }
}
data class MapRoom(val tiles: Collection<TileLocation>,
                   val id: UUID = UUID.randomUUID()){
    val outside: Boolean = tiles.contains(TileLocation(0,0)) //HACK
}
data class LogicalWall(val rooms: Collection<UUID>)
data class MapArchitecture(val rooms: Collection<MapRoom>,
                           val doors: Collection<TileLocation>,
                           var adjacencyList: Collection<RoomAdjacency>,
                           var walls: Collection<LogicalWall>)

@Singleton
class PartiallyProceduralDungeonGenerator @Inject constructor (val tiledMapInterpreter: TiledMapInterpreter,
                                                               val logicalTileTracker: LogicalTileTracker,
                                                               val tiledMapModifier: TiledMapModifier,
                                                               val mobGenerator: MobGenerator,
                                                               val actionManager: ActionManager,
                                                               val tacticalMapState: TacticalMapState,
                                                               val logicHooks: LogicHooks){
    // Requires a tilemap filled with rooms (no doors or anything else allowed.)
    // Then partitions them into rooms, and creates a graph of all the rooms that could connect to each other.
    // adds doors between 1/3 of all room adjacencies.  Then: Makes sure all rooms are connected via BFS.
    // uses an in-memory datastore to keep track of wall locations and the like.
    // REQUIRES logicalTileTracker to have been initialized.
    val roomAdjacencies = ArrayList<RoomAdjacency>()

    fun generateDungeon(scenarioParams: ScenarioParams){
        val rooms = generateRooms()
        val walls = logicalTileTracker.tiles.filter{isWall(it)}.map{it.location}
        val potentialRoomConnections = getPotentialRoomConnections(rooms, walls)

        // populate like 1/3 of the room connections
        for (conn in potentialRoomConnections){
            val rand = Random().nextInt(3)
            if (rand == 1){
                insertDoor(conn.walls.randomElement())
            }
        }
        // TODO: Add glorious inroads from the outside.

        mobGenerator.populateRooms(rooms, scenarioParams)
        populateRoomsWithOrgans(rooms.toList())

        for (char in tacticalMapState.listOfCharacters){
            logicHooks.onUnitCreation(char)
        }
    }

    private fun populateRoomsWithOrgans(rooms: List<MapRoom>) {
        val shuffled = rooms.shuffled()
        val shieldRoom  = shuffled.get(0)
        val masterRoom = shuffled.get(1)
        attemptPlacementOfMob(TacMapUnitTemplate.SHIELDING_ORGAN, shieldRoom)
        attemptPlacementOfMob(MasterOrgan(), masterRoom)
        attemptPlacementOfMob(WeakMinionSpawner(), shuffled.get(2));
    }

    fun attemptPlacementOfMob(tacMapUnitTemplate: TacMapUnitTemplate, room: MapRoom){
        val maxAttempts = 15
        for (i in 0 .. maxAttempts){
            try {
                val tileLocation = room.tiles.randomElement()
                actionManager.addCharacterToTileFromTemplate(tacMapUnitTemplate, tileLocation, false)
                return
            }catch(e: TileAlreadyOccupiedException){
                // try it again!
            }
        }
        throw IllegalStateException("Couldn't find an unoccupied tile even after $maxAttempts tries")

    }

    private fun insertDoor(tileLocation: TileLocation) {
        // TODO: Doesn't actually purge, for some reason
        tiledMapModifier.purgeTile(tileLocation, TileLayer.FEATURE)
        tiledMapModifier.placeDoor(tileLocation)
    }

    fun generateRooms() : Collection<MapRoom>{
        val accountedForTiles = HashSet<TileLocation>()
        val rooms = ArrayList<MapRoom>()
        for (tile in logicalTileTracker.tiles){
            if (accountedForTiles.contains(tile.location)){
                continue
            }
            if (isWall(tile)){
                accountedForTiles.add(tile.location)
                continue
            }
            val roomTiles = floodFill(tile)
            rooms.add(MapRoom(roomTiles))
            accountedForTiles.addAll(roomTiles)
        }
        return rooms
    }

    fun getRoomsForWall(wall: TileLocation,
                        rooms: Collection<MapRoom>) : List<MapRoom>{
        val allRooms = ArrayList<MapRoom>()
        val neighbors = logicalTileTracker.getNeighbors(wall)
        for (neighbor in neighbors){
            for (room in rooms){
                if (neighbor in room.tiles){
                    allRooms += room
                }
            }
        }
        return allRooms
    }

    fun getPotentialRoomConnections(rooms: Collection<MapRoom>,
                                    walls: Collection<TileLocation>) : List<RoomAdjacency>{
        val roomAdjacencies = ArrayList<RoomAdjacency>()
        for (wall in walls){
            val roomsForWall = getRoomsForWall(wall, rooms)
            if (roomsForWall.size != 2){
                continue // this should almost never happen
            }
            val adjacency = RoomAdjacency(roomsForWall[0].id, roomsForWall[1].id)
            if (!roomAdjacencies.contains(adjacency)){
                roomAdjacencies.add(adjacency)
            }
            val existingAdjacency = roomAdjacencies.first{it.room1 == adjacency.room1 && it.room2 == adjacency.room2}
            existingAdjacency.walls += wall
        }
        return roomAdjacencies
    }


    private fun isWall(tile: LogicalTile): Boolean {
        return logicalTileTracker.isWall(tile.location)
    }

    // Returns all the tiles reachable from the tile location without passing through a wall.
    // walls will return the empty set
    fun floodFill(tile: LogicalTile) : Set<TileLocation>{
        var toProcess = HashSet<TileLocation>()
        val accepted = HashSet<TileLocation>()
        val processed = HashSet<TileLocation>()
        toProcess.add(tile.location)
        var toProcessAfter = HashSet<TileLocation>()
        while(!toProcess.isEmpty()){
            for (s in toProcess){
                if (processed.contains(s)){
                    continue
                }
                processed.add(s)
                if (isWall(logicalTileTracker.getLogicalTileFromLocation(s)!!)){
                    continue
                }
                accepted.add(s)
                toProcessAfter.addAll(logicalTileTracker.getNeighbors(s).filter{!processed.contains(it)})
            }
            toProcess = toProcessAfter
            toProcessAfter = HashSet()
        }
        return accepted
    }

}

val random = Random()
public fun <E> Collection<E>.randomElement(): E {
    val elements = random.nextInt(this.size)
    var counter = 0
    for (item in this){
        if (elements == counter){
            return item
        }
        counter++
    }
    throw IllegalStateException("We screwed up the randomElement function")
}


