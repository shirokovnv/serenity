package graphics.rendering

import core.scene.Object
import core.scene.SceneGraph
import core.scene.TraversalType
import graphics.rendering.passes.RenderPass

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
            renderers.forEach {renderer ->
                if (renderer.supportsRenderPass(pass)) {
                    renderer.render(pass)
                }
            }
        }
    }

    fun render(sceneGraph: SceneGraph, traversalType: TraversalType) {
        objects.clear()

        sceneGraph.traverse({
            objects.add(it)
        }, traversalType)

        render(objects)
    }
}