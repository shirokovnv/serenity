package modules.water.ocean.shaders

import core.management.Resources
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class OceanFftColumnsShader: OceanFftShader() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/water/ocean/FftColumns_CS.glsl")!!,
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