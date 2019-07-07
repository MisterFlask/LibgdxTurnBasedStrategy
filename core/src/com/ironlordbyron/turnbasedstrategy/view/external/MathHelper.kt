package com.ironlordbyron.turnbasedstrategy.view.external

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.math.MathUtils
import com.ironlordbyron.turnbasedstrategy.view.external.Settings

object MathHelper {
    fun cardLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 6.0f)
            if (Math.abs(startX - targetX) < Settings.CARD_SNAP_THRESHOLD) {
                startX = targetX
            }
        }
        return startX
    }

    fun cardScaleLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 7.5f)
            if (Math.abs(startX - targetX) < 0.003f) {
                startX = targetX
            }
        }
        return startX
    }

    fun uiLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 9.0f)
            if (Math.abs(startX - targetX) < Settings.UI_SNAP_THRESHOLD) {
                startX = targetX
            }
        }
        return startX
    }

    fun orbLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 6.0f)
            if (Math.abs(startX - targetX) < Settings.UI_SNAP_THRESHOLD) {
                startX = targetX
            }
        }
        return startX
    }

    fun mouseLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 20.0f)
            if (Math.abs(startX - targetX) < Settings.UI_SNAP_THRESHOLD) {
                startX = targetX
            }
        }
        return startX
    }

    fun scaleLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 8.0f)
            if (Math.abs(startX - targetX) < 0.003f) {
                startX = targetX
            }
        }
        return startX
    }

    fun fadeLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 12.0f)
            if (Math.abs(startX - targetX) < 0.01f) {
                startX = targetX
            }
        }
        return startX
    }

    fun popLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 8.0f)
            if (Math.abs(startX - targetX) < 0.003f) {
                startX = targetX
            }
        }
        return startX
    }

    fun angleLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 12.0f)
            if (Math.abs(startX - targetX) < 0.003f) {
                startX = targetX
            }
        }
        return startX
    }

    fun slowColorLerpSnap(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 3.0f)
            if (Math.abs(startX - targetX) < 0.01f) {
                startX = targetX
            }
        }
        return startX
    }

    fun scrollSnapLerpSpeed(startX: Float, targetX: Float): Float {
        var startX = startX
        if (startX != targetX) {
            startX = MathUtils.lerp(startX, targetX, Gdx.graphics.deltaTime * 10.0f)
            if (Math.abs(startX - targetX) < Settings.UI_SNAP_THRESHOLD) {
                startX = targetX
            }
        }
        return startX
    }

    fun valueFromPercentBetween(min: Float, max: Float, percent: Float): Float {
        val diff = max - min
        return min + diff * percent
    }

    fun percentFromValueBetween(min: Float, max: Float, value: Float): Float {
        val diff = max - min
        val offset = value - min
        return offset / diff
    }
}
