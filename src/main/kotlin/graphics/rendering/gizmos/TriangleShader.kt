package graphics.rendering.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class TriangleShader : BaseShader<TriangleShader, TriangleMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Triangle_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Triangle_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Triangle_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_color")
        addUniform("u_viewProj")
    }

    override fun updateUniforms() {
        setUniform("u_color", shaderMaterial!!.color)
        setUniform("u_viewProj", shaderMaterial!!.viewProjection)
    }
}