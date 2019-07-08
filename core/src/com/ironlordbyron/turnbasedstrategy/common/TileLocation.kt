package com.ironlordbyron.turnbasedstrategy.common

import com.badlogic.gdx.maps.tiled.TiledMap
import com.ironlordbyron.turnbasedstrategy.guice.GameModuleInjector
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTile
import com.ironlordbyron.turnbasedstrategy.tiledutils.LogicalTileTracker
import com.ironlordbyron.turnbasedstrategy.tiledutils.TerrainType
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TileEntity
import com.ironlordbyron.turnbasedstrategy.tilemapinterpretation.TiledMapInterpreter


data class TileLocation(val x: Int, val y: Int){

    public override fun toString(): String{
        return "[$x,$y]"
    }
}

private val gameState: TacticalMapState by lazy {
    GameModuleInjector.generateInstance(TacticalMapState::class.java)
}
public fun TileLocation.getCharacter() : LogicalCharacter? {
    return gameState.characterAt(this)
}

public fun TileLocation.distanceTo(tileLocation: TileLocation): Int {
    return Math.abs(this.x - tileLocation.x) + Math.abs(this.y - tileLocation.y)
}

val tiledMapInterpreter: TiledMapInterpreter by lazy{
    GameModuleInjector.generateInstance(TiledMapInterpreter::class.java)
}
val tiledMapProvider: TileMapProvider by lazy{
    GameModuleInjector.generateInstance(TileMapProvider::class.java)
}
public fun TileLocation.terrainType(): TerrainType? {
    return logicalTileTracker.getLogicalTileFromLocation(this)?.terrainType
}

val logicalTileTracker: LogicalTileTracker by lazy{
    GameModuleInjector.generateInstance(LogicalTileTracker::class.java)
}

public fun TileLocation.logicalTile() : LogicalTile? {
    return logicalTileTracker.getLogicalTileFromLocation(this)
}

public fun TileLocation.entity(): TileEntity?{
    return logicalTileTracker.getEntitiesAtTile(this).firstOrNull()
}