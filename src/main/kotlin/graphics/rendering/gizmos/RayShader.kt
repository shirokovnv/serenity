package graphics.rendering.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class RayShader : BaseShader<RayShader, RayMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Ray_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Ray_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Ray_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("uRayOrigin")
        addUniform("uRayDirection")
        addUniform("uRayLength")
        addUniform("uRayColor")
        addUniform("uViewProjection")
    }

    override fun updateUniforms() {
        setUniform("uRayOrigin", shaderMaterial!!.rayOrigin)
        setUniform("uRayDirection", shaderMaterial!!.rayDirection)
        setUniformf("uRayLength", shaderMaterial!!.rayLength)
        setUniform("uRayColor", shaderMaterial!!.rayColor)
        setUniform("uViewProjection", shaderMaterial!!.viewProjection)
    }
}