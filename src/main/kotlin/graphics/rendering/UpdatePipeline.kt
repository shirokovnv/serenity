package graphics.rendering

import core.ecs.Behaviour
import core.scene.Object
import core.scene.SceneGraph
import core.scene.Timer
import core.scene.TraversalType

class UpdatePipeline(private val timer: Timer) {
    companion object {
        private val objects = mutableListOf<Object>()
    }

    fun update(objects: List<Object>) {
        objects
            .filter { it.isActive() }
            .flatMap { it -> it.getComponents<Behaviour>().filter { it.isActive() } }
            .forEach { behaviour ->
                behaviour.update(timer.deltaTime())
            }
    }

    fun update(sceneGraph: SceneGraph, traversalType: TraversalType) {
        objects.clear()

        sceneGraph.traverse({
            objects.add(it)
        }, traversalType)

        update(objects)
    }
}