package com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.tiledutils.*
import java.util.*
import javax.inject.Singleton

@Singleton
class BlankMapGenerator @Inject constructor(val fragmentCopier: TiledMapOperationsHandler,
                                            val tileTracker: LogicalTileTracker) {
    companion object {
        val DEFAULT_SCENARIO = MapGenerationParams(
                sourceMapName = "BlankGrass.tmx")
    }

    fun generateMap(params: MapGenerationParams): TiledMap {
        val blankMap = fragmentCopier.getTileMap(params.sourceMapName, MapType.SOURCE_MAP)

        /**
        val blankMapFirstLayer = blankMap.getTileLayer(TileLayer.BASE)
        val locationsUsed: HashSet<TileLocation> = hashSetOf()
        for (i in 0..params.numCities) {
            val cityTileMap = fragmentCopier.pullTileMapLayer(params.cityFragmentMapName, MapType.FRAGMENT_MAP, tileLayer = TileLayer.BASE)
            val width = cityTileMap.width
            val height = cityTileMap.height
            val randomX = Random().nextInt(blankMapFirstLayer.width - width)
            val randomY = Random().nextInt(blankMapFirstLayer.height - height)

            // fitting function
            val fits = doesItFit(width, height, locationsUsed, randomX, randomY)
            if (fits) {
                locationsUsed.addAll(getSpacesUsed(width, height, randomX, randomY))
                fragmentCopier.copyFragmentTo(params.sourceMapName, randomX, randomY, params.cityFragmentMapName)
                // TODO: Add functionality to enrich our map data with the fact that it's a city.
            }
        }
        */
        return blankMap
    }


    private fun getSpacesUsed(width: Int, height: Int, startX: Int, startY: Int): Set<TileLocation> {
        val used = HashSet<TileLocation>()
        for (x in startX..startX + width) {
            for (y in startY..startY + height) {
                used.add(TileLocation(x, y))
            }
        }
        return used
    }

    private fun doesItFit(width: Int, height: Int, locationsUsed: HashSet<TileLocation>, startX: Int, startY: Int): Boolean {
        var doesFit = true
        for (x in startX..startX + width) {
            for (y in startY..startY + height) {
                if (locationsUsed.contains(TileLocation(x, y))) {
                    doesFit = false
                }
            }
        }
        return doesFit
    }
}


data class MapGenerationParams(val sourceMapName: String)
