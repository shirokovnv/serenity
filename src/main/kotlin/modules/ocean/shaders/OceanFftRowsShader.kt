package modules.ocean.shaders

import core.scene.Object
import graphics.assets.surface.ShaderType
import modules.ocean.OceanShader
import platform.services.filesystem.TextFileLoader

class OceanFftRowsShader: OceanFftShader() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/ocean/FftRows_CS.glsl")!!,
            ShaderType.COMPUTE_SHADER
        )

        linkAndValidate()

        addUniform("u_resolution")
        addUniform("u_stride")
        addUniform("u_count")
    }

    override fun updateUniforms() {
        setUniformi("u_resolution", shaderMaterial!!.fftResolution)
        setUniformi("u_stride", shaderMaterial!!.stride)
        setUniformi("u_count", shaderMaterial!!.count)
    }
}