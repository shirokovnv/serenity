package modules.terrain.roam

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.terrain.BaseTerrainShader
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class RoamTerrainPatchShader(
    private val useInstancing: Boolean
) : BaseTerrainShader<RoamTerrainPatchShader, RoamTerrainPatchMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val frustumInc = fileLoader.loadAsString("shaders/include/Frustum.glsl")!!
        val atmosphereInc = fileLoader.loadAsString("shaders/include/Atmosphere.glsl")!!
        val geometryShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/roam/Terrain_GS.glsl")!!,
            mapOf("Frustum.glsl" to frustumInc)
        )
        val fragmentShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/roam/Terrain_FS.glsl")!!,
            mapOf("Atmosphere.glsl" to atmosphereInc)
        )

        val vertexShaderPath = if (useInstancing) "shaders/terrain/roam/Terrain_VS_Inst.glsl"
        else "shaders/terrain/roam/Terrain_VS.glsl"

        addShader(
            fileLoader.loadAsString(vertexShaderPath)!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            geometryShaderSource,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fragmentShaderSource,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_heightmap")
        addUniform("u_normalmap")
        addUniform("u_blendmap")
        addUniform("u_model")
        addUniform("u_world")
        addUniform("u_viewProj")
        addUniform("u_camPos")
        addUniform("u_textureSize")
        addUniform("u_sunVector")
        addUniform("u_sunColor")
        addUniform("u_sunIntensity")
        addUniform("u_scaleY")
    }

    override fun updateUniforms() {
        setUniform("u_model", shaderMaterial!!.model)
        setUniform("u_world", shaderMaterial!!.world)
        setUniform("u_viewProj", shaderMaterial!!.viewProjection)
        setUniform("u_camPos", shaderMaterial!!.cameraPosition)
        setUniform("u_textureSize", shaderMaterial!!.textureSize)
        setUniform("u_sunVector", shaderMaterial!!.sunVector)
        setUniform("u_sunColor", shaderMaterial!!.sunColor)
        setUniformf("u_sunIntensity", shaderMaterial!!.sunIntensity)
        setUniformf("u_scaleY", shaderMaterial!!.scaleY)

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.texture().bind()
        setUniformi("u_heightmap", 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE1)
        shaderMaterial!!.normalmap.bind()
        setUniformi("u_normalmap", 1)

        GL43.glActiveTexture(GL43.GL_TEXTURE2)
        shaderMaterial!!.blendmap.bind()
        setUniformi("u_blendmap", 2)
    }
}