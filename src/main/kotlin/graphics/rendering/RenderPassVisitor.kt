package graphics.rendering

interface RenderPassVisitor<T: Renderer> {
    fun visit(renderer: T, pass: RenderPass)
}