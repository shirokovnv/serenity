package modules.terrain.marching_cubes

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class MarchingCubesShader : BaseShader<MarchingCubesShader, MarchingCubesMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/terrain/marching_cubes/MarchingCubes_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/marching_cubes/MarchingCubes_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/marching_cubes/MarchingCubes_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_World")
        addUniform("u_ViewProjection")
        addUniform("u_LightDirection")
        addUniform("u_LightColor")
        addUniform("u_ColorOne")
        addUniform("u_ColorTwo")
        addUniform("u_AmbientStrength")
        addUniform("u_DiffuseStrength")
    }

    override fun updateUniforms() {
        setUniform("u_World", shaderMaterial!!.world)
        setUniform("u_ViewProjection", shaderMaterial!!.viewProjection)

        setUniform("u_LightDirection", shaderMaterial!!.lightDirection)
        setUniform("u_LightColor", shaderMaterial!!.lightColor)
        setUniform("u_ColorOne", shaderMaterial!!.colorOne)
        setUniform("u_ColorTwo", shaderMaterial!!.colorTwo)
        setUniformf("u_AmbientStrength", shaderMaterial!!.ambientStrength)
        setUniformf("u_DiffuseStrength", shaderMaterial!!.diffuseStrength)
    }
}