package modules.terrain.cdlod

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.terrain.BaseTerrainShader
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class CdlodTerrainShader : BaseTerrainShader<CdlodTerrainShader, CdlodTerrainMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val transformInc = fileLoader.loadAsString("shaders/include/Transform.glsl")!!
        val vertexShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/cdlod/Terrain_VS.glsl")!!,
            mapOf("Transform.glsl" to transformInc)
        )

        addShader(
            vertexShaderSource,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/cdlod/Terrain_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_heightmap")
        addUniform("u_normalMap")
        addUniform("u_blendMap")
        addUniform("u_model")
        addUniform("u_world")
        addUniform("u_viewProj")
        addUniform("u_camPos")
        addUniform("resolution")
        addUniform("u_sunVector")
        addUniform("u_sunColor")
        addUniform("u_sunIntensity")

        for (i in 0..<shaderMaterial!!.lodRanges.size) {
            addUniform("lodRanges[$i]")
        }
    }

    override fun updateUniforms() {
        setUniform("u_model", shaderMaterial!!.model)
        setUniform("u_world", shaderMaterial!!.world)
        setUniform("u_viewProj", shaderMaterial!!.viewProjection)
        setUniform("u_camPos", shaderMaterial!!.camPos)
        setUniform("u_sunVector", shaderMaterial!!.sunVector)
        setUniform("u_sunColor", shaderMaterial!!.sunColor)
        setUniformf("u_sunIntensity", shaderMaterial!!.sunIntensity)

        setUniformf("resolution", shaderMaterial!!.resolution)
        for (i in 0..<shaderMaterial!!.lodRanges.size) {
            setUniformf("lodRanges[$i]", shaderMaterial!!.lodRanges[i])
        }

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.texture().bind()
        setUniformi("u_heightmap", 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE1)
        shaderMaterial!!.normalmap.bind()
        setUniformi("u_normalMap", 1)

        GL43.glActiveTexture(GL43.GL_TEXTURE2)
        shaderMaterial!!.blendmap.bind()
        setUniformi("u_blendMap", 2)
    }
}