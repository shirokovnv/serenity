package graphics.rendering.postproc.godrays

import core.management.Resources
import core.math.Vector2
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.TextFileLoader

class GodraysPPShader : BaseShader<GodraysPPShader, GodraysPPMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/postproc/GodraysPP_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/postproc/GodraysPP_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("exposure")
        addUniform("decay")
        addUniform("density")
        addUniform("weight")
        addUniform("isLightOnScreen")
        addUniform("lightScreenPosition")
        addUniform("firstPass")
        addUniform("secondPass")
    }

    override fun updateUniforms() {
        setUniformf("exposure", shaderMaterial!!.exposure)
        setUniformf("decay", shaderMaterial!!.decay)
        setUniformf("density", shaderMaterial!!.density)
        setUniformf("weight", shaderMaterial!!.weight)

        val isLightOnScreen = if (shaderMaterial!!.lightScreenPosition != null) 1 else 0
        setUniformi("isLightOnScreen", isLightOnScreen)
        setUniform("lightScreenPosition", shaderMaterial!!.lightScreenPosition ?: Vector2(-1f, -1f))

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.firstPass.bind()
        setUniformi("firstPass", 0)

        GL43.glActiveTexture(GL43.GL_TEXTURE1)
        shaderMaterial!!.secondPass.bind()
        setUniformi("secondPass", 1)
    }
}