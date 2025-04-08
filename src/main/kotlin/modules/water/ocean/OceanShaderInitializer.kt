package modules.water.ocean

import core.management.Disposable
import modules.water.ocean.shaders.*

class OceanShaderInitializer(private val material: OceanMaterial): Disposable {
    lateinit var meshShader: OceanMeshShader private set
    lateinit var spectrum0Shader: OceanSpectrum0Shader private set
    lateinit var spectrumTShader: OceanSpectrumTShader private set
    lateinit var fftColumnsShader: OceanFftColumnsShader private set
    lateinit var fftRowsShader: OceanFftRowsShader private set
    lateinit var normalShader: OceanNormalShader private set
    lateinit var orientationShader: OceanOrientationShader private set

    init {
        meshShader = OceanMeshShader()
        spectrum0Shader = OceanSpectrum0Shader()
        spectrumTShader = OceanSpectrumTShader()
        fftColumnsShader = OceanFftColumnsShader()
        fftRowsShader = OceanFftRowsShader()
        normalShader = OceanNormalShader()
        orientationShader = OceanOrientationShader()

        meshShader.setMaterial(material)
        spectrum0Shader.setMaterial(material)
        spectrumTShader.setMaterial(material)
        fftColumnsShader.setMaterial(material)
        fftRowsShader.setMaterial(material)
        normalShader.setMaterial(material)
        orientationShader.setMaterial(material)

        meshShader.setup()
        spectrum0Shader.setup()
        spectrumTShader.setup()
        fftColumnsShader.setup()
        fftRowsShader.setup()
        normalShader.setup()
        orientationShader.setup()
    }

    override fun dispose() {
        meshShader.destroy()
        spectrum0Shader.destroy()
        spectrumTShader.destroy()
        fftColumnsShader.destroy()
        fftRowsShader.destroy()
        normalShader.destroy()
        orientationShader.destroy()
    }
}