package graphics.model

import core.ecs.BaseComponent
import core.math.Matrix4
import core.math.Quaternion
import core.scene.Object
import graphics.rendering.Renderer
import graphics.rendering.passes.*

class ModelRenderer(
    private val models: List<Model>,
    private val material: ModelMaterial,
    private val shader: ModelShader,
    private val clipPlanes: Map<RenderPass, Quaternion>,
    private val viewProjectionProvider: (() -> Matrix4)?,
    private val orthoProjectionProvider: (() -> Matrix4)?,
    private val lightViewProvider: (() -> Matrix4)?
): BaseComponent(), Renderer {

    companion object {
        private val supportedPasses = listOf(NormalPass, ShadowPass, ReflectionPass, RefractionPass)
    }

    private val viewProjection: Matrix4
        get() = viewProjectionProvider?.invoke() ?: Matrix4()

    private val worldViewProjection: Matrix4
        get() = viewProjection * worldMatrix

    private val orthoProjection: Matrix4
        get() = orthoProjectionProvider?.invoke() ?: Matrix4()

    private val lightView: Matrix4
        get() = lightViewProvider?.invoke() ?: Matrix4()

    private val worldMatrix: Matrix4
        get() = (owner()!! as Object).worldMatrix()

    private val worldLightViewProjection: Matrix4
        get() = orthoProjection * lightView * worldMatrix

    override fun render(pass: RenderPass) {
        material.clipPlane = clipPlanes[pass] ?: Quaternion(0f, -1f, 0f, 10000f)

        shader.bind()
        models.forEach { model ->
            val mtlNames = model.getMaterialNames()
            mtlNames.forEach { mtlName ->
                material.mtlData = model.getMtlDataByName(mtlName)

                material.worldMatrix = worldMatrix
                material.worldViewProjection = when(pass) {
                    ShadowPass -> worldLightViewProjection
                    else -> worldViewProjection
                }

                material.isInstanced = model.isInstanced()
                shader.updateUniforms()
                model.drawByMaterial(mtlName)
            }
        }

        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass in supportedPasses
    }
}