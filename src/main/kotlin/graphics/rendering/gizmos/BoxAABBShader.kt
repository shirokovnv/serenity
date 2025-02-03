package graphics.rendering.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class BoxAABBShader : BaseShader<BoxAABBShader, BoxAABBMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/gizmos/BoxAABB_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/BoxAABB_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/BoxAABB_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("uBoxCenter")
        addUniform("uBoxSize")
        addUniform("uColor")
        addUniform("uViewProjection")
    }

    override fun updateUniforms() {
        setUniform("uBoxCenter", shaderMaterial!!.boxCenter)
        setUniform("uBoxSize", shaderMaterial!!.boxSize)
        setUniform("uColor", shaderMaterial!!.color)
        setUniform("uViewProjection", shaderMaterial!!.viewProjection)
    }
}