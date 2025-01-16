package modules.ocean.shaders

import core.scene.Object
import graphics.assets.surface.ShaderType
import modules.ocean.OceanShader
import platform.services.filesystem.TextFileLoader

class OceanNormalShader: OceanShader() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/ocean/Normal_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()
    }

    override fun updateUniforms() {
    }
}