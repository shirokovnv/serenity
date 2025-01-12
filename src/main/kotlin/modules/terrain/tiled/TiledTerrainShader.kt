package modules.terrain.tiled

import core.scene.Object
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.TextFileLoader

class TiledTerrainShader: BaseShader<TiledTerrainShader, TiledTerrainMaterial>() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_TC.glsl")!!,
            ShaderType.TESSELLATION_CONTROL_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_TE.glsl")!!,
            ShaderType.TESSELLATION_EVALUATION_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/Terrain_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_World")
        addUniform("m_View")
        addUniform("m_ViewProjection")
        addUniform("gridScale")
        addUniform("heightmap")
        addUniform("normalmap")
        addUniform("minDistance")
        addUniform("maxDistance")
        addUniform("minLOD")
        addUniform("maxLOD")
        addUniform("scaleY")
    }

    override fun updateUniforms() {
        setUniform("m_World", shaderMaterial!!.world)
        setUniform("m_View", shaderMaterial!!.view)
        setUniform("m_ViewProjection", shaderMaterial!!.viewProjection)
        setUniformf("gridScale", shaderMaterial!!.gridScale)

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.getTexture().bind()
        setUniformi("heightmap", 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE1)
        shaderMaterial!!.normalmap.bind()
        setUniformi("normalmap", 1)

        setUniformf("minDistance", shaderMaterial!!.minDistance)
        setUniformf("maxDistance", shaderMaterial!!.maxDistance)
        setUniformf("minLOD", shaderMaterial!!.minLOD)
        setUniformf("maxLOD", shaderMaterial!!.maxLOD)
        setUniformf("scaleY", shaderMaterial!!.scaleY)
    }
}