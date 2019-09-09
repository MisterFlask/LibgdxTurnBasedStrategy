package com.ironlordbyron.turnbasedstrategy.controller

import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.common.HighlightType
import com.ironlordbyron.turnbasedstrategy.common.TileLocation
import com.ironlordbyron.turnbasedstrategy.view.animation.ActorActionPair
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.foreverHighlightBlinking
import com.ironlordbyron.turnbasedstrategy.view.animation.animationgenerators.temporaryHighlightBlinking
import com.ironlordbyron.turnbasedstrategy.tiledutils.SpriteActorFactory
import com.ironlordbyron.turnbasedstrategy.tiledutils.TiledMapOperationsHandler
import com.ironlordbyron.turnbasedstrategy.tiledutils.mapgen.TileMapProvider
import com.ironlordbyron.turnbasedstrategy.view.ActorName
import com.ironlordbyron.turnbasedstrategy.view.ActorOrdering
import com.ironlordbyron.turnbasedstrategy.view.setFunctionalName
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class MapHighlighter @Inject constructor(val tiledMapOperationsHandler: TiledMapOperationsHandler,
                                                val imageActorFactory: SpriteActorFactory,
                                                val tileMapProvider: TileMapProvider){

    private val mapOfHighlights = HashMap<String, MutableCollection<Actor>>()

    public fun highlightTiles(tiles: Collection<TileLocation>,
                       highlightType: HighlightType,
                       tag: String,
                       actionGenerator: ActionGeneratorType = ActionGeneratorType.HIGHLIGHT_UNTIL_FURTHER_NOTICE) {
        killHighlights(tag)
        for (location in tiles) {
            val actionToApply = when(actionGenerator){
                ActionGeneratorType.HIGHLIGHT_UNTIL_FURTHER_NOTICE -> foreverHighlightBlinking()
            }
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, highlightType.toTexture(),
                    alpha = .5f)
            actor.setFunctionalName(ActorName(ActorOrdering.HIGHLIGHTS))
            if (highlightType.color != null){
                actor.color = highlightType.color
            }
            actor.addAction(actionToApply)

            if (!mapOfHighlights.containsKey(tag)){
                mapOfHighlights.put(tag, arrayListOf())
            }
            mapOfHighlights.get(tag)!!.add(actor)
        }
    }

    public fun killHighlights(tag: String? = null) {
        for (item in mapOfHighlights){
            if (tag == null || tag == item.key){
                val listOfHighlights = item.value
                listOfHighlights.forEach { it.remove() }
                listOfHighlights.removeAll{true}
            }
        }
    }

    fun getTileHighlightActorActionPairs(tiles: Collection<TileLocation>,
                                         highlightType: HighlightType,
                                         cameraFocusActor: Actor? = null) : ActorActionPair {
        val actorActionPairList = ArrayList<ActorActionPair>()
        val texture = tiledMapOperationsHandler.pullGenericTexture(
                highlightType.tiledTexturePath.spriteId,
                highlightType.tiledTexturePath.tileSetName)
        for (location in tiles) {
            val action = temporaryHighlightBlinking()
            val actor = imageActorFactory.createSpriteActorForTile(tileMapProvider.tiledMap, location, texture,
                    alpha = .0f)
            if (highlightType.color != null){
                actor.color = highlightType.color
            }
            actorActionPairList.add(ActorActionPair(actor, action, murderActorsOnceCompletedAnimation = true))
        }
        val actorActionPair = ActorActionPair(actor = actorActionPairList[0].actor,
                action = actorActionPairList[0].action,
                secondaryActions = actorActionPairList.subList(1, actorActionPairList.size),
                murderActorsOnceCompletedAnimation = true,
                name="temporaryHighlights",
                cameraTrigger=true,
                cameraFocusActor = cameraFocusActor)

        return actorActionPair
    }

    public enum class ActionGeneratorType{
        HIGHLIGHT_UNTIL_FURTHER_NOTICE
    }
}
