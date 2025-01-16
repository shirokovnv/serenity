package modules.ocean.shaders

import core.scene.Object
import graphics.assets.surface.ShaderType
import modules.ocean.OceanShader
import platform.services.filesystem.TextFileLoader

class OceanSpectrum0Shader: OceanShader() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/ocean/Spectrum0_CS.glsl")!!,
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