package modules.water.ocean.shaders

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.water.ocean.OceanShader
import platform.services.filesystem.FileLoader

class OceanSpectrum0Shader: OceanShader() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/water/ocean/Spectrum0_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()

        addUniform("u_resolution")
        addUniform("u_ocean_size")
        addUniform("u_amplitude")
        addUniform("u_wind")
    }

    override fun updateUniforms() {
        setUniformi("u_resolution", shaderMaterial!!.fftResolution)
        setUniformi("u_ocean_size", shaderMaterial!!.oceanSize)
        setUniformf("u_amplitude", shaderMaterial!!.amplitude)
        setUniform("u_wind", shaderMaterial!!.wind)
    }
}