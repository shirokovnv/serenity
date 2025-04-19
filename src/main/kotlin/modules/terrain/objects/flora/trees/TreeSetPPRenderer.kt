package modules.terrain.objects.flora.trees

import core.ecs.BaseComponent
import core.math.Matrix4
import core.scene.Object
import graphics.model.Model
import graphics.rendering.Renderer
import graphics.rendering.passes.PostProcPass
import graphics.rendering.passes.RenderPass

class TreeSetPPRenderer(
    private val treeSet: MutableList<Model>,
    private val material: TreeSetPPMaterial,
    private val shader: TreeSetPPShader,
    private val viewProjectionProvider: () -> Matrix4
): BaseComponent(), Renderer {

    private val worldViewProjection: Matrix4
        get() = viewProjectionProvider() * worldMatrix

    private val worldMatrix: Matrix4
        get() = (owner()!! as Object).worldMatrix()

    override fun render(pass: RenderPass) {
        if (pass != PostProcPass) {
            return
        }

        shader.bind()
        treeSet.forEach { tree ->
            val mtlNames = tree.getMaterialNames()
            mtlNames.forEach { mtlName ->
                material.originalMaterial.mtlData = tree.getMtlDataByName(mtlName)
                material.originalMaterial.worldViewProjection = worldViewProjection

                material.originalMaterial.isInstanced = tree.isInstanced()
                shader.updateUniforms()
                tree.drawByMaterial(mtlName)
            }
        }
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == PostProcPass
    }
}