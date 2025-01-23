package modules.flora.grass

import core.management.Resources
import core.scene.camera.Camera
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import platform.services.filesystem.TextFileLoader

class GrassPatchShader: BaseShader<GrassPatchShader, GrassPatchMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<TextFileLoader>()!!

        addShader(
            fileLoader.load("shaders/flora/grass/GrassPatch_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.load("shaders/flora/grass/GrassPatch_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("worldMatrix")
        addUniform("viewMatrix")
        addUniform("projMatrix")
        addUniform("cameraPosition")
        addUniform("time")
        addUniform("sunVector")
        addUniform("sunColor")
        addUniform("sunIntensity")
    }

    override fun updateUniforms() {
        setUniform("worldMatrix", shaderMaterial!!.worldMatrix)
        setUniform("viewMatrix", shaderMaterial!!.viewMatrix)
        setUniform("projMatrix", shaderMaterial!!.projMatrix)
        setUniform("cameraPosition", Resources.get<Camera>()!!.position())
        setUniformf("time", shaderMaterial!!.time)
        setUniform("sunVector", Resources.get<SunLightManager>()!!.sunVector())
        setUniform("sunColor", Resources.get<SunLightManager>()!!.sunColor())
        setUniformf("sunIntensity", Resources.get<SunLightManager>()!!.sunIntensity())
    }
}