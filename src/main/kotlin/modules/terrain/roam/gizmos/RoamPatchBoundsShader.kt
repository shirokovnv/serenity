package modules.terrain.roam.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import org.lwjgl.opengl.GL43
import platform.services.filesystem.FileLoader

class RoamPatchBoundsShader(private val useInstancing: Boolean) :
    BaseShader<RoamPatchBoundsShader, RoamPatchBoundsMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val frustumInc = fileLoader.loadAsString("shaders/include/Frustum.glsl")!!
        val geometryShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/roam/gizmos/Bounds_GS.glsl")!!,
            mapOf("Frustum.glsl" to frustumInc)
        )

        val vertexShaderPath =
            if (useInstancing) "shaders/terrain/roam/gizmos/Bounds_VS_Inst.glsl"
            else "shaders/terrain/roam/gizmos/Bounds_VS.glsl"

        addShader(
            fileLoader.loadAsString(vertexShaderPath)!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            geometryShaderSource,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/roam/gizmos/Bounds_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_model")
        addUniform("u_world")
        addUniform("u_viewProj")
        addUniform("u_heightmap")
        addUniform("u_color")
    }

    override fun updateUniforms() {
        setUniform("u_model", shaderMaterial!!.model)
        setUniform("u_world", shaderMaterial!!.world)
        setUniform("u_viewProj", shaderMaterial!!.viewProj)
        setUniform("u_color", shaderMaterial!!.color.toVector3())

        GL43.glActiveTexture(GL43.GL_TEXTURE0)
        shaderMaterial!!.heightmap.texture().bind()
        setUniformi("u_heightmap", 0)
    }
}