package graphics.rendering.passes

import graphics.rendering.Renderer

interface RenderPassVisitor<T: Renderer> {
    fun visit(renderer: T, pass: RenderPass)
}