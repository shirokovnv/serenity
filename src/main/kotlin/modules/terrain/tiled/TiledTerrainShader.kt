package modules.terrain.tiled

import core.scene.Object
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13C
import platform.services.filesystem.TextFileLoader

class TiledTerrainShader: BaseShader<TiledTerrainShader, TiledTerrainMaterial>() {
    private var material: TiledTerrainMaterial? = null

    override fun setMaterial(material: TiledTerrainMaterial?) {
        this.material = material
    }

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
        addUniform("minDistance")
        addUniform("maxDistance")
        addUniform("minLOD")
        addUniform("maxLOD")
        addUniform("scaleY")
    }

    override fun updateUniforms() {
        setUniform("m_World", material!!.world)
        setUniform("m_View", material!!.view)
        setUniform("m_ViewProjection", material!!.viewProjection)
        setUniformf("gridScale", material!!.gridScale)

        GL13C.glActiveTexture(GL13.GL_TEXTURE0)
        material!!.heightmap.getTexture().bind()
        setUniformi("heightmap", 0)

        setUniformf("minDistance", material!!.minDistance)
        setUniformf("maxDistance", material!!.maxDistance)
        setUniformf("minLOD", material!!.minLOD)
        setUniformf("maxLOD", material!!.maxLOD)
        setUniformf("scaleY", material!!.scaleY)
    }

    override fun getMaterial(): TiledTerrainMaterial? {
        return material
    }
}