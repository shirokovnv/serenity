package graphics.rendering

import core.ecs.Component

interface Renderer : Component {
    fun render(pass: RenderPass)
    fun supportsRenderPass(pass: RenderPass): Boolean
}