package com.ironlordbyron.turnbasedstrategy.controller

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.HighlightType
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.foreverHighlightBlinking
import com.ironlordbyron.turnbasedstrategy.view.animation.temporaryHighlightBlinking
import com.ironlordbyron.turnbasedstrategy.tiledutils.SpriteActorFactory
import com.ironlordbyron.turnbasedstrategy.tiledutils.TileMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class MapHighlighter @Inject constructor(val tileMapOperationsHandler: TileMapOperationsHandler,
                                                val imageActorFactory: SpriteActorFactory,
                                                val tileMapProvider: TileMapProvider){

    private val listOfHighlights = ArrayList<Actor>()

    public fun highlightTiles(tiles: Collection<TileLocation>,
                       highlightType: HighlightType,
                       actionGenerator: ActionGeneratorType = ActionGeneratorType.HIGHLIGHT_UNTIL_FURTHER_NOTICE) {
        val texture = tileMapOperationsHandler.pullGenericTexture(
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        for (location in tiles) {
            val actionToApply = when(actionGenerator){
                ActionGeneratorType.HIGHLIGHT_UNTIL_FURTHER_NOTICE -> foreverHighlightBlinking()
            }
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, texture,
                    alpha = .5f)
            actor.addAction(actionToApply)
            listOfHighlights.add(actor)
        }
    }

    public fun killHighlights() {
        listOfHighlights.forEach { it.remove() }
        listOfHighlights.removeAll{true}
    }

    fun getTileHighlightActorActionPairs(tiles: Collection<TileLocation>,
                                         highlightType: HighlightType) : ActorActionPair {
        val actorActionPairList = ArrayList<ActorActionPair>()
        val texture = tileMapOperationsHandler.pullGenericTexture(
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        for (location in tiles) {
            val action = temporaryHighlightBlinking()
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, texture,
                    alpha = .0f)
            actorActionPairList.add(ActorActionPair(actor, action))
        }
        val actorActionPair = ActorActionPair(actor = actorActionPairList[0].actor,
                action = actorActionPairList[0].action,
                secondaryActions = actorActionPairList.subList(1, actorActionPairList.size),
                murderActorsOnceCompletedAnimation = true,
                name="temporaryHighlights")

        return actorActionPair
    }

    public enum class ActionGeneratorType{
        HIGHLIGHT_UNTIL_FURTHER_NOTICE
    }
}