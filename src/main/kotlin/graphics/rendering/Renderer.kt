package graphics.rendering

import core.ecs.Component
import graphics.rendering.passes.RenderPass

interface Renderer : Component {
    fun render(pass: RenderPass)
    fun supportsRenderPass(pass: RenderPass): Boolean
}