package modules.water.ocean.shaders

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.water.ocean.OceanShader
import platform.services.filesystem.FileLoader

class OceanOrientationShader: OceanShader() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/water/ocean/Orientation_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()

        addUniform("u_resolution")
    }

    override fun updateUniforms() {
        setUniformi("u_resolution", shaderMaterial!!.fftResolution)
    }
}