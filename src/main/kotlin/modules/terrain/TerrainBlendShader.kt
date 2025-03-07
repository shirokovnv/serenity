package modules.terrain

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class TerrainBlendShader : BaseShader<TerrainBlendShader, TerrainBlendMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/terrain/TerrainBlend_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()

        addUniform("elevationDataCount")
        addUniform("heightmap")
        addUniform("normalmap")
        addUniform("width")
        addUniform("height")

        for (i in 0..<MAX_ELEVATION_DATA_COUNT) {
            addUniform("minElevation[$i]")
            addUniform("maxElevation[$i]")
            addUniform("minSlope[$i]")
            addUniform("maxSlope[$i]")
            addUniform("strength[$i]")
        }
    }

    override fun updateUniforms() {
        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.texture().bind()
        setUniformi("heightmap", 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE1)
        shaderMaterial!!.normalmap.bind()
        setUniformi("normalmap", 1)

        setUniformi("width", shaderMaterial!!.heightmap.texture().getWidth())
        setUniformi("height", shaderMaterial!!.heightmap.texture().getHeight())

        val elevationDataCount = shaderMaterial!!.elevationData.size.coerceIn(1..MAX_ELEVATION_DATA_COUNT)

        setUniformi("elevationDataCount", elevationDataCount)

        for(i in 0..<elevationDataCount) {
            setUniformf("minElevation[$i]", shaderMaterial!!.elevationData[i].minElevation)
            setUniformf("maxElevation[$i]", shaderMaterial!!.elevationData[i].maxElevation)
            setUniformf("minSlope[$i]", shaderMaterial!!.elevationData[i].minSlope)
            setUniformf("maxSlope[$i]", shaderMaterial!!.elevationData[i].maxSlope)
            setUniformf("strength[$i]", shaderMaterial!!.elevationData[i].strength)
        }
    }
}