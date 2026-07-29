package modules.terrain.quadtree.top_view

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class QuadTreeTopViewShader: BaseShader<QuadTreeTopViewShader, QuadTreeTopViewMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val transformInc = fileLoader.loadAsString("shaders/include/Transform.glsl")!!
        val frustumInc = fileLoader.loadAsString("shaders/include/Frustum.glsl")!!
        val vertexShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/top_view/Top_View_VS.glsl")!!,
            mapOf("Transform.glsl" to transformInc)
        )
        val geometryShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/top_view/Top_View_GS.glsl")!!,
            mapOf("Frustum.glsl" to frustumInc)
        )

        addShader(
            vertexShaderSource,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            geometryShaderSource,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/top_view/Top_View_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_model")
        addUniform("u_viewProj")
    }

    override fun updateUniforms() {
        setUniform("u_model", shaderMaterial!!.model)
        setUniform("u_viewProj", shaderMaterial!!.viewProj)
    }
}