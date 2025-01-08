package graphics.shader

import core.math.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31
import kotlin.properties.Delegates

abstract class Shader {
    private var program by Delegates.notNull<Int>()
    private val uniforms = HashMap<String, Int>()

    init {
        program = glCreateProgram()

        if (program == 0) {
            throw IllegalStateException("Unable to create shader program.")
        }
    }

    fun bind() {
        glUseProgram(program)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun addUniform(uniformName: String) {
        val uniformLocation = glGetUniformLocation(program, uniformName)

        if (uniformLocation == -0x1) {
            throw IllegalArgumentException("Could not find uniform: $uniformName")
        }

        uniforms[uniformName] = uniformLocation
    }

    fun addUniformBlock(uniformName: String) {
        val uniformLocation = GL31.glGetUniformBlockIndex(program, uniformName)
        if (uniformLocation == -0x1) {
            throw IllegalArgumentException("Could not find uniform block: $uniformName")
        }

        uniforms[uniformName] = uniformLocation
    }

    fun addShader(source: String, shaderType: ShaderType) {
        val shader = glCreateShader(shaderType.value)

        if (shader == 0) {
            throw IllegalStateException("Unable to create shader.")
        }

        glShaderSource(shader, source)
        glCompileShader(shader)

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw IllegalStateException("Unable to compile shader: ${glGetShaderInfoLog(shader, 1024)}")
        }

        glAttachShader(program, shader)
    }

    fun bindUniformBlock(uniformBlockName: String, uniformBlockBinding: Int) {
        GL31.glUniformBlockBinding(program, uniforms[uniformBlockName]!!, uniformBlockBinding)
    }

    fun bindFragDataLocation(name: String, index: Int) {
        GL30.glBindFragDataLocation(program, index, name)
    }

    fun program(): Int = program

    fun setUniformi(uniformName: String, value: Int) {
        glUniform1i(uniforms[uniformName]!!, value)
    }

    fun setUniformf(uniformName: String, value: Float) {
        glUniform1f(uniforms[uniformName]!!, value)
    }

    fun setUniform(uniformName: String, value: Vector2) {
        glUniform2f(uniforms[uniformName]!!, value.x, value.y)
    }

    fun setUniform(uniformName: String, value: Vector3) {
        glUniform3f(uniforms[uniformName]!!, value.x, value.y, value.z)
    }

    fun setUniform(uniformName: String, value: Quaternion) {
        glUniform4f(uniforms[uniformName]!!, value.x, value.y, value.z, value.w)
    }

    fun setUniform(uniformName: String, value: Matrix4) {
        glUniformMatrix4fv(uniforms[uniformName]!!, true, value.toFloatBuffer())
    }
}