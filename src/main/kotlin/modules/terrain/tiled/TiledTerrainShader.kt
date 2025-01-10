package modules.terrain.tiled

import core.scene.Object
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13C
import platform.services.filesystem.TextFileLoader

class TiledTerrainShader: BaseShader<TiledTerrainShader, TiledTerrainMaterial, TiledTerrainMaterialParams>() {
    private var material: TiledTerrainMaterial? = null

    override fun setMaterial(material: TiledTerrainMaterial?) {
        this.material = material
    }

    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/terrain/tiled/TiledTerrain_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/TiledTerrain_TC.glsl")!!,
            ShaderType.TESSELLATION_CONTROL_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/TiledTerrain_TE.glsl")!!,
            ShaderType.TESSELLATION_EVALUATION_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/TiledTerrain_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.load("shaders/terrain/tiled/TiledTerrain_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_World")
        addUniform("m_View")
        addUniform("m_ViewProjection")
        addUniform("gridScale")
        addUniform("heightmap")
        addUniform("minDistance")
        addUniform("maxDistance")
        addUniform("minLOD")
        addUniform("maxLOD")
        addUniform("scaleY")
    }

    override fun updateUniforms() {
        setUniform("m_World", material!!.getParams().world)
        setUniform("m_View", material!!.getParams().view)
        setUniform("m_ViewProjection", material!!.getParams().viewProjection)
        setUniformf("gridScale", material!!.getParams().gridScale)

        GL13C.glActiveTexture(GL13.GL_TEXTURE0)
        material!!.getParams().heightmap.getTexture().bind()
        setUniformi("heightmap", 0)

        setUniformf("minDistance", material!!.getParams().minDistance)
        setUniformf("maxDistance", material!!.getParams().maxDistance)
        setUniformf("minLOD", material!!.getParams().minLOD)
        setUniformf("maxLOD", material!!.getParams().maxLOD)
        setUniformf("scaleY", material!!.getParams().scaleY)
    }

    override fun getMaterial(): TiledTerrainMaterial? {
        return material
    }
}