package modules.terrain.objects.fauna

import core.management.Resources
import core.math.Quaternion
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import platform.services.filesystem.FileLoader

class AnimalShader : BaseShader<AnimalShader, AnimalMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/terrain/fauna/Animal_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/fauna/Animal_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("model")
        addUniform("view")
        addUniform("projection")
        addUniform("sunVector")
        addUniform("sunColor")
        addUniform("sunIntensity")
        addUniform("diffuseColor")
        addUniform("isShadowPass")

        for (i in 0..<200) {
            addUniform("bones[$i]")
        }
    }

    override fun updateUniforms() {
        val sunLightManager = Resources.get<SunLightManager>()!!
        val diffuseColor = shaderMaterial!!.mtlData?.diffuseColor?.toQuaternion() ?: Quaternion(1f, 1f, 1f, 1f)

        setUniform("model", shaderMaterial!!.model)
        setUniform("view", shaderMaterial!!.view)
        setUniform("projection", shaderMaterial!!.projection)
        setUniform("sunVector", sunLightManager.sunVector())
        setUniform("sunColor", sunLightManager.sunColor())
        setUniformf("sunIntensity", sunLightManager.sunIntensity())
        setUniform("diffuseColor", diffuseColor)
        setUniformi("isShadowPass", if (shaderMaterial!!.isShadowPass) 1 else 0)

        shaderMaterial!!.boneTransforms.take(200).withIndex().forEach { (i, v) ->
            setUniform("bones[$i]", v)
        }
    }
}