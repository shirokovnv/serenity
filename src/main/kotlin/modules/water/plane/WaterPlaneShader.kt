package modules.water.plane

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class WaterPlaneShader : BaseShader<WaterPlaneShader, WaterPlaneMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        addShader(
            fileLoader.loadAsString("shaders/water/plane/WaterPlane_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/water/plane/WaterPlane_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_worldHeight")
        addUniform("u_worldMatrix")
        addUniform("u_localMatrix")
        addUniform("u_viewProjection")
    }

    override fun updateUniforms() {
        setUniform("u_localMatrix", shaderMaterial!!.localMatrix)
        setUniform("u_worldMatrix", shaderMaterial!!.worldMatrix)
        setUniform("u_viewProjection", shaderMaterial!!.viewProjection)
        setUniformf("u_worldHeight", shaderMaterial!!.worldHeight)
    }
}