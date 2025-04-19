package modules.terrain.objects.flora.grass

import core.management.Resources
import core.scene.camera.Camera
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import modules.light.SunLightManager
import modules.terrain.heightmap.Heightmap
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class GrassPatchShader: BaseShader<GrassPatchShader, GrassPatchMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/terrain/flora/grass/GrassPatch_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/flora/grass/GrassPatch_FS.glsl")!!,
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
        addUniform("heightmap")
        addUniform("worldScale")
        addUniform("worldOffset")
    }

    override fun updateUniforms() {
        val heightmap = Resources.get<Heightmap>()!!

        setUniform("worldMatrix", shaderMaterial!!.worldMatrix)
        setUniform("viewMatrix", shaderMaterial!!.viewMatrix)
        setUniform("projMatrix", shaderMaterial!!.projMatrix)
        setUniform("cameraPosition", Resources.get<Camera>()!!.position())
        setUniformf("time", shaderMaterial!!.time)
        setUniform("sunVector", Resources.get<SunLightManager>()!!.sunVector())
        setUniform("sunColor", Resources.get<SunLightManager>()!!.sunColor())
        setUniformf("sunIntensity", Resources.get<SunLightManager>()!!.sunIntensity())
        setUniform("worldScale", heightmap.worldScale())
        setUniform("worldOffset", heightmap.worldOffset())

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        heightmap.texture().bind()
        setUniformi("heightmap", 0)
    }
}