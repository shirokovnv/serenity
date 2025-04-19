package graphics.model

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.FileLoader

class ModelShader : BaseShader<ModelShader, ModelMaterial>() {

    companion object {
        private const val AMBIENT_TEXTURE_UNIT = 0
        private const val DIFFUSE_TEXTURE_UNIT = 1
        private const val NORMAL_TEXTURE_UNIT = 2
        private const val SPECULAR_TEXTURE_UNIT = 3
    }

    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/model/Model_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/model/Model_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate(::beforeValidationCallback)

        addUniform("m_WorldMatrix")
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
        addUniform("opacity")
        addUniform("alphaThreshold")
        addUniform("sunVector")
        addUniform("sunColor")
        addUniform("sunIntensity")
        addUniform("isShadowPass")
    }

    override fun updateUniforms() {
        setUniform("m_WorldMatrix", shaderMaterial!!.worldMatrix)
        setUniform("m_WorldViewProjection", shaderMaterial!!.worldViewProjection)
        setUniformi("isInstanced", if (shaderMaterial!!.isInstanced) 1 else 0)
        setUniformf("opacity", shaderMaterial!!.opacity)
        setUniformf("alphaThreshold", shaderMaterial!!.alphaThreshold)
        setUniform("sunVector", Resources.get<SunLightManager>()!!.sunVector())
        setUniform("sunColor", Resources.get<SunLightManager>()!!.sunColor())
        setUniformf("sunIntensity", Resources.get<SunLightManager>()!!.sunIntensity())
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
            setUniformi("ambientMap", AMBIENT_TEXTURE_UNIT)
        }

        if (isDiffuseMapUsed == 1) {
            glActiveTexture(GL_TEXTURE1)
            shaderMaterial!!.mtlData?.getTexture(TextureType.DIFFUSE_TEXTURE)?.bind()
            setUniformi("diffuseMap", DIFFUSE_TEXTURE_UNIT)
        }

        if (isNormalMapUsed == 1) {
            glActiveTexture(GL_TEXTURE2)
            shaderMaterial!!.mtlData?.getTexture(TextureType.NORMAL_TEXTURE)?.bind()
            setUniformi("normalMap", NORMAL_TEXTURE_UNIT)
        }

        if (isSpecularMapUsed == 1) {
            glActiveTexture(GL_TEXTURE3)
            shaderMaterial!!.mtlData?.getTexture(TextureType.SPECULAR_TEXTURE)?.bind()
            setUniformi("specularMap", SPECULAR_TEXTURE_UNIT)
        }
    }

    private fun beforeValidationCallback() {
        val shaderProgram = getId()

        val ambientMapLoc = glGetUniformLocation(shaderProgram, "ambientMap")
        val diffuseMapLoc = glGetUniformLocation(shaderProgram, "diffuseMap")
        val normalMapLoc = glGetUniformLocation(shaderProgram, "normalMap")
        val specularMapLoc = glGetUniformLocation(shaderProgram, "specularMap")

        glUseProgram(shaderProgram)
        glUniform1i(ambientMapLoc, AMBIENT_TEXTURE_UNIT)
        glUniform1i(diffuseMapLoc, DIFFUSE_TEXTURE_UNIT)
        glUniform1i(normalMapLoc, NORMAL_TEXTURE_UNIT)
        glUniform1i(specularMapLoc, SPECULAR_TEXTURE_UNIT)
        glUseProgram(0)
    }
}