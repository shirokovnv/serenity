package modules.ocean.shaders

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.ocean.OceanShader
import platform.services.filesystem.FileLoader

class OceanNormalShader: OceanShader() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/ocean/Normal_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()
    }

    override fun updateUniforms() {
    }
}