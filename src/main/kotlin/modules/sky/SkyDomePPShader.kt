package modules.sky

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import platform.services.filesystem.TextFileLoader

class SkyDomePPShader: BaseShader<SkyDomePPShader, SkyDomePPMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<TextFileLoader>()!!

        val atmosphereInc = fileLoader.load("shaders/include/Atmosphere.glsl")!!
        val vertexSource = preprocessShader(
            fileLoader.load("shaders/sky/SkyDomePP_VS.glsl")!!,
            mapOf("Atmosphere.glsl" to atmosphereInc)
        )

        addShader(
            vertexSource,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/sky/SkyDomePP_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_WorldViewProjection")
        addUniform("sunVector")
        addUniform("sunColor")
        addUniform("sunIntensity")
    }

    override fun updateUniforms() {
        setUniform("m_WorldViewProjection", shaderMaterial!!.worldViewProjection)
        setUniform("sunVector", Resources.get<SunLightManager>()!!.sunVector())
        setUniform("sunColor", Resources.get<SunLightManager>()!!.sunColor())
        setUniformf("sunIntensity", Resources.get<SunLightManager>()!!.sunIntensity())
    }
}