package modules.terrain.navigation

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class TerrainNavMeshShader : BaseShader<TerrainNavMeshShader, TerrainNavMeshMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/terrain/navigation/NavMesh_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/navigation/NavMesh_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("uViewProjection")
        addUniform("uyOffset")
        addUniform("uOpacity")
    }

    override fun updateUniforms() {
        setUniform("uViewProjection", shaderMaterial!!.viewProjection)
        setUniformf("uyOffset", shaderMaterial!!.yOffset)
        setUniformf("uOpacity", shaderMaterial!!.opacity)
    }
}