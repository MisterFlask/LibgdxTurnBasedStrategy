package com.ironlordbyron.turnbasedstrategy.view.animation.external


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import java.util.*
import kotlin.math.pow

class LaserEffectFactory(){
    fun generateLaserEffect(actor1 : Actor, actor2: Actor) : LaserEffect{
        return LaserEffect(
                position = Vector2(actor1.x, actor1.y),
                distance = actor1.distanceTo(actor2).toFloat(),
                degrees = actor1.degreesOffsetTo(actor2))
    }
}

private fun Actor.degreesOffsetTo(actor2: Actor): Float {
    val degrees = Math.atan2(
            actor2.getY().toDouble() - this.getY().toDouble(),
            actor2.getX().toDouble() - this.getX().toDouble()
    ) * 180.0 / Math.PI
    return degrees.toFloat()
}

private fun Actor.distanceTo(actor2: Actor): Double {
    return Math.sqrt((actor2.x - this.x).pow(2).toDouble() + (actor2.y - this.y).pow(2).toDouble())
}

class LaserEffect(
        val guid: UUID = UUID.randomUUID(),
        var position: Vector2 = Vector2(),
            var distance: Float = 0.toFloat(),
            var color: Color = Color(Color.RED),
            var rayColor : Color = Color(Color.WHITE),
            var degrees: Float = 0.toFloat(),
            var begin1: Sprite = Sprite(Texture(Gdx.files.internal("data/beamstart1.png"))),
            var begin2: Sprite = Sprite(Texture(Gdx.files.internal("data/beamstart2.png"))),
            var mid1: Sprite = Sprite(Texture(Gdx.files.internal("data/beammid1.png"))),
             var mid2: Sprite = Sprite(Texture(Gdx.files.internal("data/beamend1.png"))),
             var end1: Sprite = Sprite(Texture(Gdx.files.internal("data/beamend1.png"))),
             var end2: Sprite = Sprite(Texture(Gdx.files.internal("data/beamend2.png")))) {

    fun render(delta: Float, batch: SpriteBatch) {

        begin1.color = color
        begin2.color = rayColor
        mid1.color = color
        mid2.color = rayColor
        end1.color = color
        end2.color = rayColor

        mid1.setSize(mid1!!.width, distance)
        mid2.setSize(mid1!!.width, distance)

        begin1.setPosition(position.x, position.y)
        begin2.setPosition(position.x, position.y)

        mid1.setPosition(begin1!!.x, begin1!!.y + begin1!!.height)
        mid2.setPosition(begin1!!.x, begin1!!.y + begin1!!.height)

        end1.setPosition(begin1!!.x, begin1!!.y + begin1!!.height + mid1!!.height)
        end2.setPosition(begin1!!.x, begin1!!.y + begin1!!.height + mid1!!.height)

        begin1.setOrigin(begin1!!.width / 2, 0f)
        begin2.setOrigin(begin1!!.width / 2, 0f)


        mid1.setOrigin(mid1!!.width / 2, -begin1!!.height)
        mid2.setOrigin(mid2!!.width / 2, -begin1!!.height)
        end1.setOrigin(mid1!!.width / 2, -begin1!!.height - mid1!!.height)
        end2.setOrigin(mid2!!.width / 2, -begin1!!.height - mid2!!.height)


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
}
