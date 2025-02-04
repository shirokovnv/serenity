package graphics.rendering.gizmos

import core.events.Event
import graphics.rendering.passes.RenderPass

data class DrawGizmosEvent(val pass: RenderPass) : Event