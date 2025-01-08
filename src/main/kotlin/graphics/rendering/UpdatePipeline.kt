package graphics.rendering

import core.ecs.Behaviour
import core.scene.Object
import core.scene.Timer

class UpdatePipeline(private val timer: Timer) {
    fun update(objects: List<Object>) {
        objects
            .filter { it.isActive() }
            .flatMap { it -> it.getComponents<Behaviour>().filter { it.isActive() } }
            .forEach { behaviour ->
                behaviour.update(timer.deltaTime())
            }
    }
}