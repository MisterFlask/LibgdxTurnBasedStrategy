package com.ironlordbyron.turnbasedstrategy.view.tiledutils

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader

class FragmentCopier {
    fun copyFragmentTo(gameMapLayer: TiledMapTileLayer,
                       minX: Int,
                       minY: Int,
                       fragmentName: String) {
        return copyTo(gameMapLayer,
                pullTileMapLayer(fragmentName),
                minX,
                minY)
    }

    private fun copyTo(gameMapLayer: TiledMapTileLayer,
                       fragmentMapLayer: TiledMapTileLayer,
                       minX: Int, minY: Int) {
        for (x in minX..fragmentMapLayer.width + minX) {
            for (y in minY..fragmentMapLayer.height + minY) {
                val fragmentX = x - minX
                val fragmentY = y - minY
                val fragmentCell = fragmentMapLayer.getCell(fragmentX, fragmentY)
                if (fragmentCell != null){
                    gameMapLayer.setCell(x, y, fragmentCell)
                }
            }
        }
    }
}

enum class TileLayer{
    TERRAIN, FEATURE,
}


fun TiledMap.GetTileLayer(layer: TileLayer) : TiledMapTileLayer{
   val num = when (layer){
       TileLayer.TERRAIN -> 0
       TileLayer.FEATURE -> 1
   }
    return this.layers[num] as TiledMapTileLayer
}

private val precursor = "tilesets/fragments"
private fun pullTileMapLayer(name: String): TiledMapTileLayer {
    return TmxMapLoader()
            .load("${precursor}/${name}")
            .layers
            .get(0) as TiledMapTileLayer
}

object TileMapFragment {


    val City = "CityFragment.tmx"

}