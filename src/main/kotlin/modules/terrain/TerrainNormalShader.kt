package modules.terrain

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.TextFileLoader

class TerrainNormalShader : BaseShader<TerrainNormalShader, TerrainNormalMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/terrain/TerrainNormal_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()

        addUniform("heightmap")
        addUniform("width")
        addUniform("height")
        addUniform("normalStrength")
    }

    override fun updateUniforms() {
        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.texture().bind()
        setUniformi("heightmap", 0)

        setUniformi("width", shaderMaterial!!.heightmap.texture().getWidth())
        setUniformi("height", shaderMaterial!!.heightmap.texture().getHeight())
        setUniformf("normalStrength", shaderMaterial!!.normalStrength)
    }
}