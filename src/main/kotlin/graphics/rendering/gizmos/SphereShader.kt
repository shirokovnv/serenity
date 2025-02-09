package graphics.rendering.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class SphereShader : BaseShader<SphereShader, SphereMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Sphere_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Sphere_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/gizmos/Sphere_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("uSphereCenter")
        addUniform("uSphereRadius")
        addUniform("uSphereColor")
        addUniform("uViewProjection")
    }

    override fun updateUniforms() {
        setUniform("uSphereCenter", shaderMaterial!!.center)
        setUniformf("uSphereRadius", shaderMaterial!!.radius)
        setUniform("uSphereColor", shaderMaterial!!.color)
        setUniform("uViewProjection", shaderMaterial!!.viewProjection)
    }
}