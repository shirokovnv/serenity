package graphics.rendering.postproc.identity

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class IdentityPPShader: BaseShader<IdentityPPShader, IdentityPPMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/postproc/IdentityPP_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/postproc/IdentityPP_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("colorTexture")
    }

    override fun updateUniforms() {
        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.colorTexture.bind()
        setUniformi("colorTexture", 0)
    }
}