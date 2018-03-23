package com.ironlordbyron.turnbasedstrategy.view.tiledutils.mapgen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.google.inject.Inject
import com.ironlordbyron.turnbasedstrategy.view.tiledutils.*
import java.util.*
import javax.inject.Singleton

@Singleton
public class BlankMapGenerator @Inject constructor(val fragmentCopier: TileMapOperationsHandler) {
    companion object {
        public val defaultMapGenParams = MapGenerationParams(numCities = 3,
                sourceMapName = "BlankGrass.tmx",
                cityFragmentMapName = "CityFragment.tmx")
    }

    fun generateMap(params: MapGenerationParams): TiledMap {
        val blankMap = fragmentCopier.getTileMap(params.sourceMapName, isFragment = false)
        val blankMapFirstLayer = blankMap.getTileLayer(TileLayer.BASE)
        val locationsUsed: HashSet<TileLocation> = hashSetOf()
        for (i in 0..params.numCities) {
            val cityTileMap = fragmentCopier.pullTileMapLayer(params.cityFragmentMapName, isFragment = true, tileLayer = TileLayer.BASE)
            val width = cityTileMap.width
            val height = cityTileMap.height
            val randomX = Random().nextInt(blankMapFirstLayer.width - width)
            val randomY = Random().nextInt(blankMapFirstLayer.height - height)

            // fitting function
            val fits = doesItFit(width, height, locationsUsed, randomX, randomY)
            if (fits) {
                locationsUsed.addAll(getSpacesUsed(width, height, randomX, randomY))
                fragmentCopier.copyFragmentTo(params.sourceMapName, randomX, randomY, params.cityFragmentMapName)
            }
        }
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


data class MapGenerationParams(val numCities: Int,
                               val sourceMapName: String,
                               val cityFragmentMapName: String = TileMapFragment.City) {

}
