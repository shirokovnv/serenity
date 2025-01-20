package modules.ocean.shaders

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.ocean.OceanShader
import platform.services.filesystem.TextFileLoader

class OceanSpectrumTShader: OceanShader() {
    override fun setup() {
        val fileLoader = Resources.get<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/ocean/SpectrumT_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()

        addUniform("u_resolution")
        addUniform("u_ocean_size")
        addUniform("u_choppiness")
        addUniform("u_time")
    }

    override fun updateUniforms() {
        setUniformi("u_resolution", shaderMaterial!!.fftResolution)
        setUniformi("u_ocean_size", shaderMaterial!!.oceanSize)
        setUniformf("u_choppiness", shaderMaterial!!.choppiness)
        setUniformf("u_time", shaderMaterial!!.time)
    }
}