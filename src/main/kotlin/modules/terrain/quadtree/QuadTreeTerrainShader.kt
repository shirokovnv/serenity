package modules.terrain.quadtree

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.terrain.BaseTerrainShader
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class QuadTreeTerrainShader: BaseTerrainShader<QuadTreeTerrainShader, QuadTreeTerrainMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val transformInc = fileLoader.loadAsString("shaders/include/Transform.glsl")!!
        val vertexShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/Terrain_VS.glsl")!!,
            mapOf("Transform.glsl" to transformInc)
        )

        addShader(
            vertexShaderSource,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/Terrain_TC.glsl")!!,
            ShaderType.TESSELLATION_CONTROL_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/Terrain_TE.glsl")!!,
            ShaderType.TESSELLATION_EVALUATION_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/Terrain_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/Terrain_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_heightmap")
        addUniform("u_model")
        addUniform("u_world")
        addUniform("u_viewProj")
        addUniform("u_scaleY")
        addUniform("u_tessFactor")
        addUniform("u_normalMap")
        addUniform("u_blendMap")
        addUniform("u_sunColor")
        addUniform("u_sunVector")
        addUniform("u_sunIntensity")
        addUniform("u_camPos")
    }

    override fun updateUniforms() {
        setUniform("u_model", shaderMaterial!!.model)
        setUniform("u_world", shaderMaterial!!.world)
        setUniform("u_viewProj", shaderMaterial!!.viewProjection)
        setUniformf("u_scaleY", shaderMaterial!!.scaleY)
        setUniformi("u_tessFactor", shaderMaterial!!.tessFactor)
        setUniform("u_sunColor", shaderMaterial!!.sunColor)
        setUniform("u_sunVector", shaderMaterial!!.sunVector)
        setUniformf("u_sunIntensity", shaderMaterial!!.sunIntensity)
        setUniform("u_camPos", shaderMaterial!!.camPos)

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