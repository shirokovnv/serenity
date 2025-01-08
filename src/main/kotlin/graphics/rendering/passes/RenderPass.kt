package graphics.rendering.passes

interface RenderPass {
    val name: String
    fun start()
    fun finish()
}

