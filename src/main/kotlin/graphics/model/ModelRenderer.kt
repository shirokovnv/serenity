package graphics.model

import core.ecs.BaseComponent
import core.math.Matrix4
import core.scene.Object
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import graphics.rendering.passes.ShadowPass

class ModelRenderer(
    private val models: List<Model>,
    private val material: ModelMaterial,
    private val shader: ModelShader,
    private val viewProjectionProvider: (() -> Matrix4)?,
    private val orthoProjectionProvider: (() -> Matrix4)?,
    private val lightViewProvider: (() -> Matrix4)?
): BaseComponent(), Renderer {

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
        shader.bind()
        models.forEach { model ->
            val mtlNames = model.getMaterialNames()
            mtlNames.forEach { mtlName ->
                material.mtlData = model.getMtlDataByName(mtlName)

                when(pass) {
                    NormalPass -> material.worldViewProjection = worldViewProjection
                    ShadowPass -> material.worldViewProjection = worldLightViewProjection
                }

                material.isInstanced = model.isInstanced()
                shader.updateUniforms()
                model.drawByMaterial(mtlName)
            }
        }

        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass || pass == ShadowPass
    }
}