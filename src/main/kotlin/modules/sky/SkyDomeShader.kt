package modules.sky

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.FileLoader

class SkyDomeShader: BaseShader<SkyDomeShader, SkyDomeMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val atmosphereInc = fileLoader.loadAsString("shaders/include/Atmosphere.glsl")!!
        val vertexSource = preprocessShader(
            fileLoader.loadAsString("shaders/sky/SkyDome_VS.glsl")!!,
            mapOf("Atmosphere.glsl" to atmosphereInc)
        )

        addShader(
            vertexSource,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/sky/SkyDome_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_WorldViewProjection")
        addUniform("cloudTexture")
        addUniform("sunVector")
        addUniform("sunColor")
        addUniform("sunIntensity")
        addUniform("cloudAnimationOffset")
    }

    override fun updateUniforms() {
        setUniform("m_WorldViewProjection", shaderMaterial!!.worldViewProjection)
        setUniform("cloudAnimationOffset", shaderMaterial!!.cloudAnimationOffset)

        glActiveTexture(GL_TEXTURE0)
        shaderMaterial!!.cloudTexture.bind()
        setUniformi("cloudTexture", 0)

        setUniform("sunVector", Resources.get<SunLightManager>()!!.sunVector())
        setUniform("sunColor", Resources.get<SunLightManager>()!!.sunColor())
        setUniformf("sunIntensity", Resources.get<SunLightManager>()!!.sunIntensity())
    }
}