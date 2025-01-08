package graphics.rendering

abstract class BaseRenderer: Renderer {
    override fun render(pass: RenderPass) {
        val visitor = createRenderPassVisitor(pass)
        visitor?.visit(this, pass)
    }

    abstract fun createRenderPassVisitor(pass: RenderPass): RenderPassVisitor<Renderer>?
}