package modules.flora.palm

import core.scene.Object
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.TextFileLoader

class PalmShader: BaseShader<PalmShader, PalmMaterial>() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/flora/SimpleModel_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/flora/SimpleModel_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_WorldViewProjection")
    }

    override fun updateUniforms() {
        setUniform("m_WorldViewProjection", shaderMaterial!!.worldViewProjection)
    }
}