package modules.flora.trees

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import graphics.model.TextureType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class TreeSetPPShader: BaseShader<TreeSetPPShader, TreeSetPPMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/flora/trees/TreesPP_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/flora/trees/TreesPP_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("m_WorldViewProjection")
        addUniform("isInstanced")
        addUniform("diffuseMap")
        addUniform("alphaThreshold")
    }

    override fun updateUniforms() {
        setUniform("m_WorldViewProjection", shaderMaterial!!.originalMaterial.worldViewProjection)
        setUniformi("isInstanced", if (shaderMaterial!!.originalMaterial.isInstanced) 1 else 0)
        setUniformf("alphaThreshold", shaderMaterial!!.originalMaterial.alphaThreshold)

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.originalMaterial.mtlData?.getTexture(TextureType.DIFFUSE_TEXTURE)?.bind()
        setUniformi("diffuseMap", 0)
    }
}