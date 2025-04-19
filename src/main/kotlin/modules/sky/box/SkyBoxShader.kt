package modules.sky.box

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class SkyBoxShader : BaseShader<SkyBoxShader, SkyBoxMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/sky/box/SkyBox_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/sky/box/SkyBox_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_World")
        addUniform("u_View")
        addUniform("u_Projection")
        addUniform("u_CubemapTexture")
    }

    override fun updateUniforms() {
        setUniform("u_World", shaderMaterial!!.world)
        setUniform("u_View", shaderMaterial!!.view)
        setUniform("u_Projection", shaderMaterial!!.projection)

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.cubemapTexture.bind()
        setUniformi("u_CubemapTexture", 0)
    }
}