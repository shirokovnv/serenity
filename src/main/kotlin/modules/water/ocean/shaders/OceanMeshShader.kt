package modules.water.ocean.shaders

import core.management.Resources
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import modules.water.ocean.OceanShader
import org.lwjgl.opengl.GL43.*
import platform.services.filesystem.FileLoader

class OceanMeshShader: OceanShader() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/water/ocean/Mesh_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/water/ocean/Mesh_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("model")
        addUniform("view")
        addUniform("projection")
        addUniform("offset_position")
        addUniform("u_displacement_map")
        addUniform("u_normal_map")
        addUniform("u_resolution")
        addUniform("sunVector")
        addUniform("sunIntensity")
        addUniform("sunColor")
    }

    override fun updateUniforms() {
        setUniformi("u_resolution", shaderMaterial!!.fftResolution)
        setUniform("model", shaderMaterial!!.model)
        setUniform("view", shaderMaterial!!.view)
        setUniform("projection", shaderMaterial!!.projection)
        setUniform("offset_position", shaderMaterial!!.offsetPosition)

        glActiveTexture(GL_TEXTURE0)
        shaderMaterial!!.displacementMap.bind()
        setUniformi("u_displacement_map", 0)

        glActiveTexture(GL_TEXTURE1)
        shaderMaterial!!.normalMap.bind()
        setUniformi("u_normal_map", 1)

        setUniform("sunVector", Resources.get<SunLightManager>()!!.sunVector())
        setUniformf("sunIntensity", Resources.get<SunLightManager>()!!.sunIntensity())
        setUniform("sunColor", Resources.get<SunLightManager>()!!.sunColor())
    }
}