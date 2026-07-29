package modules.terrain.quadtree.gizmos

import core.management.Resources
import graphics.assets.surface.BaseShader
import graphics.assets.surface.ShaderType
import platform.services.filesystem.FileLoader

class QuadTreeBoundsShader: BaseShader<QuadTreeBoundsShader, QuadTreeBoundsMaterial>() {
    override fun setup() {
        val fileLoader = Resources.get<FileLoader>()!!

        val frustumInc = fileLoader.loadAsString("shaders/include/Frustum.glsl")!!
        val geometryShaderSource = preprocessShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/gizmos/Bounds_GS.glsl")!!,
            mapOf("Frustum.glsl" to frustumInc)
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/gizmos/Bounds_VS.glsl")!!,
            ShaderType.VERTEX_SHADER
        )

        addShader(
            geometryShaderSource,
            ShaderType.GEOMETRY_SHADER
        )

        addShader(
            fileLoader.loadAsString("shaders/terrain/quadtree/gizmos/Bounds_FS.glsl")!!,
            ShaderType.FRAGMENT_SHADER
        )

        linkAndValidate()

        addUniform("u_viewProj")
        addUniform("u_color")
    }

    override fun updateUniforms() {
        setUniform("u_viewProj", shaderMaterial!!.viewProj)
        setUniform("u_color", shaderMaterial!!.color.toVector3())
    }
}