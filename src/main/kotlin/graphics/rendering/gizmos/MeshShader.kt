package graphics.rendering.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class MeshShader : BaseShader<MeshShader, MeshMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Mesh_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Mesh_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Mesh_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_ViewProjection")
        addUniform("u_Color")
    }

    override fun updateUniforms() {
        setUniform("u_ViewProjection", shaderMaterial!!.viewProjection)
        setUniform("u_Color", shaderMaterial!!.color)
    }
}