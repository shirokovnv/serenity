package modules.ocean.shaders

import core.scene.Object
import graphics.assets.surface.ShaderType
import modules.ocean.OceanShader
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.TextFileLoader

class OceanMeshShader: OceanShader() {
    override fun setup() {
        val fileLoader = Object.services.getService<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/ocean/Mesh_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/ocean/Mesh_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("model")
        addUniform("view")
        addUniform("projection")
        addUniform("u_displacement_map")
        addUniform("u_normal_map")
        addUniform("u_resolution")
        addUniform("u_color_texture")
    }

    override fun updateUniforms() {
        setUniformi("u_resolution", shaderMaterial!!.fftResolution)
        setUniform("model", shaderMaterial!!.model)
        setUniform("view", shaderMaterial!!.view)
        setUniform("projection", shaderMaterial!!.projection)

        glActiveTexture(GL_TEXTURE0)
        shaderMaterial!!.displacementMap.bind()
        setUniformi("u_displacement_map", 0)

        glActiveTexture(GL_TEXTURE1)
        shaderMaterial!!.normalMap.bind()
        setUniformi("u_normal_map", 1)

        glActiveTexture(GL_TEXTURE2)
        shaderMaterial!!.colorMap.bind()
        setUniformi("u_color_texture", 2)
    }
}