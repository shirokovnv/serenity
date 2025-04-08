package modules.water.plane

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43.*
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
        addUniform("u_reflectionMap")
        addUniform("u_refractionMap")
        addUniform("u_dudvMap")
        addUniform("u_normalMap")
        addUniform("u_depthMap")
        addUniform("u_moveFactor")
        addUniform("u_cameraPosition")
        addUniform("u_lightPosition")
        addUniform("u_lightColor")
        addUniform("u_near")
        addUniform("u_far")
    }

    override fun updateUniforms() {
        setUniform("u_localMatrix", shaderMaterial!!.localMatrix)
        setUniform("u_worldMatrix", shaderMaterial!!.worldMatrix)
        setUniform("u_viewProjection", shaderMaterial!!.viewProjection)
        setUniform("u_cameraPosition", shaderMaterial!!.cameraPosition)
        setUniform("u_lightPosition", shaderMaterial!!.lightPosition)
        setUniform("u_lightColor", shaderMaterial!!.lightColor)

        setUniformf("u_worldHeight", shaderMaterial!!.worldHeight)
        setUniformf("u_moveFactor", shaderMaterial!!.moveFactor)
        setUniformf("u_near", shaderMaterial!!.near)
        setUniformf("u_far", shaderMaterial!!.far)

        glActiveTexture(GL_TEXTURE0)
        shaderMaterial!!.reflectionMap.bind()
        setUniformi("u_reflectionMap", 0)

        glActiveTexture(GL_TEXTURE1)
        shaderMaterial!!.refractionMap.bind()
        setUniformi("u_refractionMap", 1)

        glActiveTexture(GL_TEXTURE2)
        shaderMaterial!!.dudvMap.bind()
        setUniformi("u_dudvMap", 2)

        glActiveTexture(GL_TEXTURE3)
        shaderMaterial!!.normalMap.bind()
        setUniformi("u_normalMap", 3)

        glActiveTexture(GL_TEXTURE4)
        shaderMaterial!!.depthMap.bind()
        setUniformi("u_depthMap", 4)
    }
}