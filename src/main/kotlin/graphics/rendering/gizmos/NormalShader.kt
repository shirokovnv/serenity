package graphics.rendering.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class NormalShader : BaseShader<NormalShader, NormalMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Normal_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Normal_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Normal_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_World")
        addUniform("u_ViewProjection")
        addUniform("u_Color")
        addUniform("u_Opacity")
    }

    override fun updateUniforms() {
        setUniform("u_World", shaderMaterial!!.world)
        setUniform("u_ViewProjection", shaderMaterial!!.viewProjection)
        setUniform("u_Color", shaderMaterial!!.color)
        setUniformf("u_Opacity", shaderMaterial!!.opacity)
    }
}