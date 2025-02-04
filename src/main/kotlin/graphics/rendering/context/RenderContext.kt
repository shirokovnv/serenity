package graphics.rendering.context

import core.events.Events
import graphics.rendering.gizmos.DrawGizmosEvent
import graphics.rendering.passes.RenderPass

object RenderContext {
    var onDrawGizmos: Boolean = false
    var onDrawWireframe: Boolean = false

    fun dispatchOnDrawGizmos(pass: RenderPass) {
        if (onDrawGizmos) {
            Events.publish(DrawGizmosEvent(pass), this)
        }
    }
}