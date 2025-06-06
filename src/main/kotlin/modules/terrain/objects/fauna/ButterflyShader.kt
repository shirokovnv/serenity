package modules.terrain.objects.fauna

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class ButterflyShader: BaseShader<ButterflyShader, ButterflyMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/terrain/fauna/Butterfly_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/fauna/Butterfly_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("model")
        addUniform("view")
        addUniform("projection")
        addUniform("sunVector")
        addUniform("sunColor")
        addUniform("sunIntensity")
        addUniform("diffuseTexture")

        for (i in 0..<200) {
            addUniform("bones[$i]")
        }
    }

    override fun updateUniforms() {
        val sunLightManager = Resources.get<SunLightManager>()!!

        setUniform("model", shaderMaterial!!.model)
        setUniform("view", shaderMaterial!!.view)
        setUniform("projection", shaderMaterial!!.projection)
        setUniform("sunVector", sunLightManager.sunVector())
        setUniform("sunColor", sunLightManager.sunColor())
        setUniformf("sunIntensity", sunLightManager.sunIntensity())

        shaderMaterial!!.boneTransforms.take(200).withIndex().forEach { (i, v) ->
            setUniform("bones[$i]", v)
        }

        if (shaderMaterial!!.diffuseTexture != null) {
            GL43.glActiveTexture(GL43.GL_TEXTURE0)
            shaderMaterial!!.diffuseTexture!!.bind()
            setUniformi("diffuseTexture", 0)
        }
    }
}