package modules.light.flare

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.TextFileLoader

class LensFlareShader: BaseShader<LensFlareShader, LensFlareMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/light/LensFlare_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/light/LensFlare_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("transform")
        addUniform("activeFlare")
        addUniform("brightness")
    }

    override fun updateUniforms() {
        setUniform("transform", shaderMaterial!!.transform)
        setUniformf("brightness", shaderMaterial!!.brightness)

        if (shaderMaterial!!.activeFlare != null) {
            glActiveTexture(GL_TEXTURE0)
            shaderMaterial!!.activeFlare!!.getTexture().bind()
            setUniformi("activeFlare", 0)
        }
    }
}