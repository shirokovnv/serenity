package graphics.assets.surface

import core.ecs.BaseComponent
import core.math.*
import graphics.assets.Asset
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31

abstract class BaseShader<Self : BaseShader<Self, T>, T : BaseMaterial<T, Self>> : BaseComponent(), Asset {
    private var programId: Int = 0
    private val uniforms = HashMap<String, Int>()
    private val shaderIds = mutableListOf<Int>()

    init {
        create()
    }

    abstract fun setup()
    abstract fun updateUniforms()

    abstract fun setMaterial(material: T?)
    abstract fun getMaterial(): T?

    override fun getId(): Int {
        return programId
    }

    final override fun create() {
        programId = glCreateProgram()

        if (programId == 0) {
            throw IllegalStateException("Unable to create shader program.")
        }
    }

    override fun destroy() {
        if (programId != 0) {
            shaderIds
                .filter { shaderId -> shaderId != 0 }
                .forEach { shaderId ->
                    glDetachShader(programId, shaderId)
                }
            glDeleteProgram(programId)
        }

        programId = 0
        shaderIds.clear()
    }

    override fun bind() {
        glUseProgram(programId)
    }

    override fun unbind() {
        glUseProgram(0)
    }

    fun addUniform(uniformName: String) {
        val uniformLocation = glGetUniformLocation(programId, uniformName)

        if (uniformLocation == -0x1) {
            throw IllegalArgumentException("Could not find uniform: $uniformName")
        }

        uniforms[uniformName] = uniformLocation
    }

    fun addUniformBlock(uniformName: String) {
        val uniformLocation = GL31.glGetUniformBlockIndex(programId, uniformName)
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

        shaderIds.add(shader)
        glAttachShader(programId, shader)
    }

    fun linkAndValidate() {
        glLinkProgram(programId)

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw IllegalStateException("Unable to link program: ${glGetProgramInfoLog(programId, 1024)}")
        }

        glValidateProgram(programId)

        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            throw IllegalStateException("Unable to validate program: ${glGetProgramInfoLog(programId, 1024)}")
        }
    }

    fun bindUniformBlock(uniformBlockName: String, uniformBlockBinding: Int) {
        GL31.glUniformBlockBinding(programId, uniforms[uniformBlockName]!!, uniformBlockBinding)
    }

    fun bindFragDataLocation(name: String, index: Int) {
        GL30.glBindFragDataLocation(programId, index, name)
    }

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

infix fun <Self : BaseShader<Self, T>, T : BaseMaterial<T, Self>> Self.bind(material: T): Self {
    this.setMaterial(material)
    material.setShader(this)
    return this
}