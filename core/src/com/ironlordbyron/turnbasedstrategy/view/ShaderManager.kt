package com.ironlordbyron.turnbasedstrategy.view

import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector2
import com.ironlordbyron.turnbasedstrategy.tiledutils.TacticalTiledMapStageProvider
import javax.inject.Inject

public class ShaderFactory @Inject constructor (val tiledMapStageProvider: TacticalTiledMapStageProvider){

    fun generateOutlineShaderOfColor(color: Color,
                                     outlineSize: Float): ShaderProgram {
        val viewport = tiledMapStageProvider.tiledMapStage.viewport
        val width = viewport.worldWidth
        val height =  viewport.worldHeight
        val shaderParamFunction = OutlineShaderParamsCreationFunction(color, outlineSize, width, height)
        val program = ShaderProgram(
                Shaders.outlineShader_vertex.shaderString,
                Shaders.outlineShader_fragment.shaderString
        )
        if (!program.isCompiled()) throw GdxRuntimeException("Couldn't compile shader: " + program.getLog())
        program.begin()
        shaderParamFunction.setShaderParams(program)
        program.end()
        return program
    }
}

/* Example usage:
fun applyShader(spriteBatch: SpriteBatch){
    // ... previous draw calls ...
    spriteBatch.end()
    shaderOutline.begin()
    shaderOutline.setUniformf("u_viewportInverse", Vector2(1f / viewPortWidth, 1f / viewportHeight))
    shaderOutline.setUniformf("u_offset", outlineSize)
    shaderOutline.setUniformf("u_step", Math.min(1f, viewPortWidth / 70f))
    shaderOutline.setUniformf("u_color", Vector3(red.toFloat(), green.toFloat(), blue.toFloat()))
    shaderOutline.end()
    spriteBatch.setShader(shaderOutline)
    spriteBatch.begin()
    spriteBatch.draw(textureRegion, x, y, viewPortWidth, viewportHeight, viewPortWidth, viewportHeight, 1f, 1f, angle)
    spriteBatch.end()
    spriteBatch.setShader(null)
    spriteBatch.begin()
// ... next draw calls ...
}
*/

data class ShaderFile(val name: String, val folder: String){
    val shaderString: String
    init{
        shaderString = toShaderString()
    }

    private fun toShaderString() : String{
        return Gdx.files.internal("shaders/${folder}/${name}").readString()
    }
}


interface ShaderParamsCreationFunction{
    fun setShaderParams(shader: ShaderProgram)
}

/**
 *
// The inverse of the viewport dimensions along X and Y
uniform vec2 u_viewportInverse;

// Color of the outline
uniform vec3 u_color;

// Thickness of the outline
uniform float u_offset;

// Step to check for neighbors
uniform float u_step;
 */
class OutlineShaderParamsCreationFunction(val color: Color, val outlineSize: Float, val viewPortWidth: Float, val viewportHeight: Float) : ShaderParamsCreationFunction{
    override fun setShaderParams(shader: ShaderProgram) : Unit{
        ShaderProgram.pedantic = false
        shader.setUniformf("u_viewportInverse", Vector2(1f / viewPortWidth, 1f / viewportHeight))
        shader.setUniformf("u_offset", outlineSize)
        shader.setUniformf("u_step", Math.min(1f, viewPortWidth / 70f))
        shader.setUniformf("u_color", Vector3(color.r.toFloat(), color.g.toFloat(), color.b.toFloat()))
    }
}
/*
class ShaderApplicator(val vertexShader: ShaderFile,
                                   val fragmentShader: ShaderFile,
                                   val setParams: ShaderParamsCreationFunction){
    fun apply(batch: Batch, drawCommand: () -> Unit){
        val shaderProgram = ShaderProgram(vertexShader.shaderString, fragmentShader.shaderString)
        shaderProgram.begin()
        setParams.setShaderParams(shaderProgram)
        shaderProgram.end()
        batch.shader = shaderProgram
        batch.begin()
        drawCommand()
        batch.end()
        batch.shader = null
    }
}
*/
object Shaders{
    val outlineShader_fragment = ShaderFile("outline_fragment_shader", "outline")
    val outlineShader_vertex = ShaderFile("outline_vertex_shader", "outline")
}