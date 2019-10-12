package com.ironlordbyron.turnbasedstrategy.view.animation.external


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ironlordbyron.turnbasedstrategy.tiledutils.StageProvider
import com.ironlordbyron.turnbasedstrategy.view.ActorName
import com.ironlordbyron.turnbasedstrategy.view.ActorOrdering
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.DataDrivenOnePageAnimation
import java.util.*
import javax.inject.Singleton
import kotlin.math.pow
import com.ironlordbyron.turnbasedstrategy.view.animation.datadriven.ProtoActor
import com.ironlordbyron.turnbasedstrategy.view.setFunctionalName
import com.kotcrab.vis.ui.building.utilities.Alignment
import javax.inject.Inject
import kotlin.math.absoluteValue


@Singleton
class SpecialEffectManager @Inject constructor(val stageProvider: StageProvider){
    // val lasers = ArrayList<LaserEffect>()
    val lines = ArrayList<LineEffect>()
    val spriteBatch: SpriteBatch = SpriteBatch()

    fun generateLaserEffect(actor1 : Actor, actor2: Actor) : LineEffect{
        // TODO
        /*
        val laser = LaserEffect(
                position = Vector2(actor1.x, actor1.y),
                distance = actor1.distanceTo(actor2).toFloat(),
                degrees = actor1.degreesOffsetTo(actor2))
        lasers.add(laser)
        laser.activated = true //todo
        */
        val lineEffect = LineEffect(actor1, actor2, DataDrivenOnePageAnimation.LASER, lineSettings = LineSettings.DEFAULT)
        stageProvider.tiledMapStage.addActor(lineEffect.actor)
        lineEffect.actor.setFunctionalName(ActorName(ActorOrdering.ABOVE_FOG_OF_WAR))

        lines.add(lineEffect)
        return lineEffect
    }

    fun destroyLineEffect(laserId: UUID){
        val line = lines.first{it.guid == laserId}
        line.actor.remove()
        this.lines.remove(line)
    }


    var deltaTime: Float = 0.toFloat()

    fun renderSpecialEffects() {
        deltaTime = Gdx.graphics.deltaTime

        spriteBatch.begin()
        spriteBatch.end()
    }


}

private fun Actor.degreesOffsetTo(actor2: Actor): Float {
    val degrees = Math.atan2(
            this.getY().toDouble() - actor2.getY().toDouble(),
            this.getX().toDouble() - actor2.getX().toDouble()
    ) * 180.0 / Math.PI
    return degrees.toFloat()
}

private fun Actor.distanceTo(actor2: Actor): Double {

    return Math.sqrt((actor2.x - this.x).pow(2).toDouble() + (actor2.y - this.y).pow(2).toDouble())
}

public fun Actor.toVector(): Vector2{
    return Vector2(this.x, this.y)
}

public class LineSettings(val startsFromOutsideOfActor: Boolean,
                          val endsOutsideOfActor: Boolean,
                          val lineWidth: Float = 3f){
    companion object {
        val DEFAULT = LineSettings(startsFromOutsideOfActor = true,
                endsOutsideOfActor = true,
                lineWidth = 3f)
    }
}

// creates and adds a "line" effect between two points on the stage.
// The line is going to be an instance of the provided protoActor, between the startActor and endActor.
// NOTE:  ProtoActor is assumed to be in the up-and-down orientation.
class LineEffect(val startActor: Actor,
                 val endActor: Actor,
                 val protoActor: ProtoActor,
                 val guid: UUID = UUID.randomUUID(),
                 val lineSettings: LineSettings){
    val actor = this.create(lineSettings)

    fun create(lineSettings: LineSettings) : Actor {
        val lineActor = protoActor.toActorWrapper().actor
        lineActor.height = startActor.distanceTo(endActor).absoluteValue.toFloat() - startActor.width
        lineActor.width = lineSettings.lineWidth.toFloat()
        val startCoords = startActor.getXYCoordinatesClosestTo(endActor)
        lineActor.x = startCoords.x
        lineActor.y = startCoords.y
        lineActor.setOrigin(Alignment.BOTTOM.alignment)
        lineActor.rotation = startActor.degreesOffsetTo(endActor) + 90.0f
        return lineActor
    }
}

fun Actor.getXYCoordinatesClosestTo(toward: Actor) : Vector2{
    var x = this.x
    var y = this.y
    if (toward.x > this.x){
        x = this.x + this.width
    }
    if (toward.y > this.y) {
        y = this.y + this.height
    }
    return Vector2(x,y)
}

/*
class LaserEffect(
        val guid: UUID = UUID.randomUUID(),
        var position: Vector2 = Vector2(),
            var distance: Float = 0.toFloat(),
            var color: Color = Color(Color.RED),
            var rayColor : Color = Color(Color.WHITE),
            var degrees: Float = 0.toFloat(),
            var begin1: Sprite = Sprite(Texture(Gdx.files.internal("animations/laserparts/beamstart1.png"))),
            var begin2: Sprite = Sprite(Texture(Gdx.files.internal("animations/laserparts/beamstart2.png"))),
            var mid1: Sprite = Sprite(Texture(Gdx.files.internal("animations/laserparts/beammid1.png"))),
             var mid2: Sprite = Sprite(Texture(Gdx.files.internal("animations/laserparts/beamend1.png"))),
             var end1: Sprite = Sprite(Texture(Gdx.files.internal("animations/laserparts/beamend1.png"))),
             var end2: Sprite = Sprite(Texture(Gdx.files.internal("animations/laserparts/beamend2.png")))) {

    public var activated: Boolean = false
    fun render(delta: Float, batch: SpriteBatch) {

        begin1.color = color
        begin2.color = rayColor
        mid1.color = color
        mid2.color = rayColor
        end1.color = color
        end2.color = rayColor

        mid1.setSize(mid1!!.viewPortWidth, distance)
        mid2.setSize(mid1!!.viewPortWidth, distance)

        begin1.setPosition(position.x, position.y)
        begin2.setPosition(position.x, position.y)

        mid1.setPosition(begin1!!.x, begin1!!.y + begin1!!.viewportHeight)
        mid2.setPosition(begin1!!.x, begin1!!.y + begin1!!.viewportHeight)

        end1.setPosition(begin1!!.x, begin1!!.y + begin1!!.viewportHeight + mid1!!.viewportHeight)
        end2.setPosition(begin1!!.x, begin1!!.y + begin1!!.viewportHeight + mid1!!.viewportHeight)

        begin1.setOrigin(begin1!!.viewPortWidth / 2, 0f)
        begin2.setOrigin(begin1!!.viewPortWidth / 2, 0f)


        mid1.setOrigin(mid1!!.viewPortWidth / 2, -begin1!!.viewportHeight)
        mid2.setOrigin(mid2!!.viewPortWidth / 2, -begin1!!.viewportHeight)
        end1.setOrigin(mid1!!.viewPortWidth / 2, -begin1!!.viewportHeight - mid1!!.viewportHeight)
        end2.setOrigin(mid2!!.viewPortWidth / 2, -begin1!!.viewportHeight - mid2!!.viewportHeight)


        begin1.rotation = degrees
        begin2.rotation = degrees
        mid1.rotation = degrees
        mid2.rotation = degrees
        end1.rotation = degrees
        end2.rotation = degrees

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)

        begin1!!.draw(batch)
        begin2!!.draw(batch)


        mid1!!.draw(batch)

        mid2!!.draw(batch)

        end1!!.draw(batch)
        end2!!.draw(batch)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)


    }
*/

