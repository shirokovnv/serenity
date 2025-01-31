package modules.fauna

import core.management.Resources
import core.math.Vector3
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class ButterflyShader: BaseShader<ButterflyShader, ButterflyMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/fauna/Butterfly_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/fauna/Butterfly_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("model")
        addUniform("view")
        addUniform("projection")
        addUniform("lightPos")
        addUniform("lightColor")
        addUniform("objectColor")
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
        setUniform("lightPos", sunLightManager.sunVector())
        setUniform("lightColor", sunLightManager.sunColor())
        setUniform("objectColor", Vector3(1.0f))

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