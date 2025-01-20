package graphics.model

import core.scene.Object
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.TextFileLoader

class ModelShader: BaseShader<ModelShader, ModelMaterial>() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/model/Model_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/model/Model_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_WorldViewProjection")
        addUniform("isInstanced")
        addUniform("ambientMap")
        addUniform("diffuseMap")
        addUniform("normalMap")
        addUniform("specularMap")
        addUniform("isAmbientMapUsed")
        addUniform("isDiffuseMapUsed")
        addUniform("isNormalMapUsed")
        addUniform("isSpecularMapUsed")
        addUniform("alphaThreshold")
        addUniform("sunVector")
        addUniform("sunColor")
        addUniform("sunIntensity")
        addUniform("isShadowPass")
    }

    override fun updateUniforms() {
        setUniform("m_WorldViewProjection", shaderMaterial!!.worldViewProjection)
        setUniformi("isInstanced", if (shaderMaterial!!.isInstanced) 1 else 0)
        setUniformf("alphaThreshold", shaderMaterial!!.alphaThreshold)
        setUniform("sunVector", Object.services.getService<SunLightManager>()!!.sunVector())
        setUniform("sunColor", Object.services.getService<SunLightManager>()!!.sunColor())
        setUniformf("sunIntensity", Object.services.getService<SunLightManager>()!!.sunIntensity())
        setUniformi("isShadowPass", if (shaderMaterial!!.isShadowPass) 1 else 0)

        val isAmbientMapUsed = if (shaderMaterial!!.mtlData?.getTexture(TextureType.AMBIENT_TEXTURE) != null) 1 else 0
        val isDiffuseMapUsed = if (shaderMaterial!!.mtlData?.getTexture(TextureType.DIFFUSE_TEXTURE) != null) 1 else 0
        val isNormalMapUsed = if (shaderMaterial!!.mtlData?.getTexture(TextureType.NORMAL_TEXTURE) != null) 1 else 0
        val isSpecularMapUsed = if (shaderMaterial!!.mtlData?.getTexture(TextureType.SPECULAR_TEXTURE) != null) 1 else 0

        setUniformi("isAmbientMapUsed", isAmbientMapUsed)
        setUniformi("isDiffuseMapUsed", isDiffuseMapUsed)
        setUniformi("isNormalMapUsed", isNormalMapUsed)
        setUniformi("isSpecularMapUsed", isSpecularMapUsed)

        if (isAmbientMapUsed == 1) {
            glActiveTexture(GL_TEXTURE0)
            shaderMaterial!!.mtlData?.getTexture(TextureType.AMBIENT_TEXTURE)?.bind()
            setUniformi("ambientMap", 0)
        }

        if (isDiffuseMapUsed == 1) {
            glActiveTexture(GL_TEXTURE1)
            shaderMaterial!!.mtlData?.getTexture(TextureType.DIFFUSE_TEXTURE)?.bind()
            setUniformi("diffuseMap", 1)
        }

        if (isNormalMapUsed == 1) {
            glActiveTexture(GL_TEXTURE2)
            shaderMaterial!!.mtlData?.getTexture(TextureType.NORMAL_TEXTURE)?.bind()
            setUniformi("normalMap", 2)
        }

        if (isSpecularMapUsed == 1) {
            glActiveTexture(GL_TEXTURE3)
            shaderMaterial!!.mtlData?.getTexture(TextureType.SPECULAR_TEXTURE)?.bind()
            setUniformi("specularMap", 3)
        }
    }
}