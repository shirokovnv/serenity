package modules.water.ocean.shaders

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.water.ocean.OceanShader
import platform.services.filesystem.FileLoader

class OceanNormalShader: OceanShader() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/water/ocean/Normal_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()
    }

    override fun updateUniforms() {
    }
}