package graphics.rendering

import core.ecs.Behaviour
import core.scene.Object
import core.scene.SceneGraph
import core.scene.Timer
import core.scene.TraversalOrder

class UpdatePipeline(private val timer: Timer) {
    companion object {
        private val objects = mutableListOf<Object>()
    }

    private val behaviourMap = mutableMapOf<Behaviour, Boolean>()

    fun update(objects: List<Object>) {
        objects
            .filter { it.isActive() }
            .flatMap { it -> it.getComponents<Behaviour>().filter { it.isActive() } }
            .forEach { behaviour ->
                // before update we need to ensure behaviour is created
                if (!behaviourMap.containsKey(behaviour) || behaviourMap[behaviour] == false) {
                    behaviour.create()
                    behaviourMap[behaviour] = true
                }
                behaviour.update(timer.deltaTime())
            }
    }

    fun update(sceneGraph: SceneGraph, traversalOrder: TraversalOrder) {
        objects.clear()

        sceneGraph.traverse({
            objects.add(it)
        }, traversalOrder)

        update(objects)
    }
}