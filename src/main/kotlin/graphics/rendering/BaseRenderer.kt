package graphics.rendering

import graphics.rendering.passes.RenderPass
import graphics.rendering.passes.RenderPassVisitor

abstract class BaseRenderer: Renderer {
    override fun render(pass: RenderPass) {
        val visitor = createRenderPassVisitor(pass)
        visitor?.visit(this, pass)
    }

    abstract fun createRenderPassVisitor(pass: RenderPass): RenderPassVisitor<Renderer>?
}