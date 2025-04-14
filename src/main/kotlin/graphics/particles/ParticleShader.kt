package graphics.particles

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
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
    }

    override fun updateUniforms() {
        setUniform("u_Model", shaderMaterial!!.model)
        setUniform("u_View", shaderMaterial!!.view)
        setUniform("u_Projection", shaderMaterial!!.projection)
    }
}