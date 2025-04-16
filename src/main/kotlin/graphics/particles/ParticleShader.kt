package graphics.particles

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class ParticleShader : BaseShader<ParticleShader, ParticleMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/particles/Particle_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/particles/Particle_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/particles/Particle_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_Model")
        addUniform("u_View")
        addUniform("u_Projection")
        addUniform("u_Texture")
        addUniform("u_HasTexture")
        addUniform("u_TexNumRows")
    }

    override fun updateUniforms() {
        setUniform("u_Model", shaderMaterial!!.model)
        setUniform("u_View", shaderMaterial!!.view)
        setUniform("u_Projection", shaderMaterial!!.projection)

        val hasTexture = if (shaderMaterial!!.texture != null) 1 else 0
        setUniformi("u_HasTexture", hasTexture)
        setUniformi("u_TexNumRows", shaderMaterial!!.textureNumRows)

        if (hasTexture == 1) {
            GL43.glActiveTexture(GL43.GL_TEXTURE0)
            shaderMaterial!!.texture!!.bind()
            setUniformi("u_Texture", 0)
        }
    }
}