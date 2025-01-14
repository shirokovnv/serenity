package modules.sky

import core.scene.Object
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.TextFileLoader

class SkyDomeShader: BaseShader<SkyDomeShader, SkyDomeMaterial>() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/sky/SkyDome_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/sky/SkyDome_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_WorldViewProjection")
        addUniform("cloudTexture")
    }

    override fun updateUniforms() {
        setUniform("m_WorldViewProjection", shaderMaterial!!.worldViewProjection)

        glActiveTexture(GL_TEXTURE0)
        shaderMaterial!!.cloudTexture.bind()
        setUniformi("cloudTexture", 0)
    }
}