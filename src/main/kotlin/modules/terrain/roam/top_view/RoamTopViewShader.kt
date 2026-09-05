package modules.terrain.roam.top_view

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class RoamTopViewShader(private val useInstancing: Boolean) : BaseShader<RoamTopViewShader, RoamTopViewMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val vertexShaderSource = if (useInstancing)
            "shaders/terrain/roam/top_view/Top_View_VS_Inst.glsl"
        else "shaders/terrain/roam/top_view/Top_View_VS.glsl"

        addShader(
            fileLoader.loadAsString(vertexShaderSource)!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/roam/top_view/Top_View_GS.glsl")!!,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/roam/top_view/Top_View_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_model")
    }

    override fun updateUniforms() {
        setUniform("u_model", shaderMaterial!!.model)
    }
}