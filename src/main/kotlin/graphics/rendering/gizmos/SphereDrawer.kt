package graphics.rendering.gizmos

import core.ecs.BaseComponent
import core.management.Disposable
import core.math.Sphere
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.rendering.Color
import graphics.rendering.ColorGenerator
import graphics.rendering.Drawable

class SphereDrawer(
    private val camera: Camera,
    private val sphereProvider: () -> MutableList<Sphere>,
    private val color: Color? = null
) : BaseComponent(), Drawable, Disposable {
    companion object {
        private var referenceCounter = 0
        private var buffer: PointBuffer? = null
        private var material: SphereMaterial? = null
        private var shader: SphereShader? = null
    }

    init {
        referenceCounter++

        if (buffer == null) {
            buffer = PointBuffer()
        }
        if (material == null) {
            material = SphereMaterial()
        }
        if (shader == null) {
            shader = SphereShader()
            shader!! bind material!!
            shader!!.setup()
        }
    }

    override fun dispose() {
        referenceCounter--
        if (referenceCounter <= 0) {
            buffer?.destroy()
            shader?.destroy()
        }
    }

    override fun draw() {
        material?.color = color?.toVector3() ?: ColorGenerator.fromUUID(owner()!!.id).toVector3()
        material?.viewProjection = camera.viewProjection

        shader?.bind()
        sphereProvider().forEach { sphere ->
            material?.center = sphere.center
            material?.radius = sphere.radius

            shader?.updateUniforms()
            buffer?.draw()
        }
        shader?.unbind()
    }
}