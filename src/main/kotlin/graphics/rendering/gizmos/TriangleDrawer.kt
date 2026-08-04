package graphics.rendering.gizmos

import core.ecs.BaseComponent
import graphics.rendering.Drawable

class TriangleDrawer(
    private val buffer: TriangleBuffer,
    private val shader: TriangleShader,
    private val material: TriangleMaterial
) : BaseComponent(), Drawable {
    override fun draw() {
        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.draw()
        buffer.unbind()
        shader.unbind()
    }
}