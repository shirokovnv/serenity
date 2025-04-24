package graphics.rendering.gizmos

import core.ecs.BaseComponent
import core.management.Disposable
import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.bind
import graphics.rendering.Colors
import graphics.rendering.Drawable

class NormalDrawer(
    private val buffer: Drawable,
    private val worldProvider: () -> Matrix4,
    private val viewProjectionProvider: () -> Matrix4,
    private val color: Vector3 = Colors.Red.toVector3(),
    private val opacity: Float = 0.5f
) : BaseComponent(), Drawable, Disposable {

    private var shader: NormalShader = NormalShader()
    private var material: NormalMaterial = NormalMaterial()

    init {
        shader bind material
        shader.setup()
    }

    override fun draw() {
        material.world = worldProvider()
        material.viewProjection = viewProjectionProvider()
        material.color = color
        material.opacity = opacity

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()
    }

    override fun dispose() {
        shader.destroy()
    }
}