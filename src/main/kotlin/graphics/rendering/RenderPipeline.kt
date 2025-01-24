package graphics.rendering

import core.scene.Object
import core.scene.SceneGraph
import core.scene.TraversalOrder
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import graphics.rendering.postproc.PostProcessor

class RenderPipeline {
    private val renderPasses = mutableListOf<RenderPass>()

    companion object {
        private val objects = mutableListOf<Object>()
    }

    fun addRenderPass(pass: RenderPass) {
        renderPasses.add(pass)
    }

    fun removeRenderPass(pass: RenderPass) {
        renderPasses.removeIf { it == pass }
    }

    fun clearRenderPasses() {
        renderPasses.clear()
    }

    fun render(objects: List<Object>) {
        val renderers = objects
            .filter { it.isActive() }
            .flatMap { it -> it.getComponents<Renderer>().filter { it.isActive() } }
            .toList()

        renderPasses.forEach {pass ->
            pass.start()
            renderers.forEach {renderer ->
                if (renderer.supportsRenderPass(pass)) {
                    renderer.render(pass)
                }
            }
            pass.finish()
        }

        PostProcessor.process(NormalPass.getColorTexture())
    }

    fun render(sceneGraph: SceneGraph, traversalOrder: TraversalOrder) {
        objects.clear()

        sceneGraph.traverse({
            objects.add(it)
        }, traversalOrder)

        render(objects)
    }
}