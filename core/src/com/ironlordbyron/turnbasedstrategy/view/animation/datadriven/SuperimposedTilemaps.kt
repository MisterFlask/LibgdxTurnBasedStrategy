package com.ironlordbyron.turnbasedstrategy.view.animation.datadriven

import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.xml.TilemapXmlProcessor
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImage
import com.ironlordbyron.turnbasedstrategy.view.animation.AnimatedImageParams

/**
 * These represent animations that are basically subsections of several different tilesets.  (e.g. Player0 and Player1
 * in the Dawnlike section.)
 */
data class SuperimposedTilemaps(val tileMapWithTextureName: String = COMMON_TILE_MAP,
                                val tileSetNames: List<String>,
                                val textureId: String,
                                override val orientation: OrientationType = OrientationType.NEUTRAL) : ProtoActor {

    override fun toActorWrapper(animatedImageParams: AnimatedImageParams): AnimatedImage {
        val anim = AnimatedImage.fromTextureRegions(TiledMapOperationsHandler(TilemapXmlProcessor()).pullTextures(this),
                animatedImageParams)
        return anim
    }

    companion object {
        val DOORS = listOf("Door0", "Door1")
        val DEMONS = listOf("Demon0", "Demon1")
        val SLIMES = listOf("Slime0", "Slime1")
        val PLAYER_TILE_SETS = listOf("Player0", "Player1")
        val PLANTS = listOf("Plant0", "Plant1")
        val COMMON_TILE_MAP = "tilesets/MASTER_TILESET.tmx"
        val ELEMENTALS = listOf("Elemental0","Elemental1")
        public fun toDefaultProtoActor(): ProtoActor {
            return SuperimposedTilemaps(
                    tileSetNames = PLAYER_TILE_SETS,
                    textureId = "8")
        }

        public fun doorImageNumber(i: String): SuperimposedTilemaps {
            return SuperimposedTilemaps(tileSetNames = DOORS,
                    textureId = i)
        }
        public fun weaponImageNumber(i: String): SuperimposedTilemaps {
            return SuperimposedTilemaps(tileSetNames = listOf("MedWep"),
                    textureId = i)
        }
        public fun demonImageNumber(i: String): SuperimposedTilemaps {
            return SuperimposedTilemaps(tileSetNames = DEMONS,
                    textureId = i)
        }
        public fun slimeImageNumber(i: String): SuperimposedTilemaps {
            return SuperimposedTilemaps(tileSetNames = SLIMES,
                    textureId = i)
        }
        public fun playerImageNumber(i: String): SuperimposedTilemaps {
            return SuperimposedTilemaps(tileSetNames = PLAYER_TILE_SETS,
                    textureId = i)
        }
        public fun plantImageNumber(i: String): SuperimposedTilemaps {
            return SuperimposedTilemaps(tileSetNames = PLANTS,
                    textureId = i)
        }

        public fun elementalImageNumber(i: String): SuperimposedTilemaps {
            return SuperimposedTilemaps(tileSetNames = ELEMENTALS,
                    textureId = i)
        }

    }
}