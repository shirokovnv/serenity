package modules.terrain.quadtree.gizmos

import core.ecs.BaseComponent
import graphics.rendering.Drawable
import modules.terrain.quadtree.QuadTreeBuffer

class QuadTreeBoundsDrawer(
    private val buffer: QuadTreeBuffer,
    private val material: QuadTreeBoundsMaterial,
    private val shader: QuadTreeBoundsShader
): BaseComponent(), Drawable {

    override fun draw() {
        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.primitiveType = QuadTreeBuffer.PrimitiveType.TRIANGLE
        buffer.draw()
        buffer.unbind()
        shader.unbind()
    }
}