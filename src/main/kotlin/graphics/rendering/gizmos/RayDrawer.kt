package graphics.rendering.gizmos

import core.ecs.BaseComponent
import core.management.Disposable
import core.scene.camera.Camera
import core.scene.raytracing.RayData
import graphics.assets.surface.bind
import graphics.rendering.Color
import graphics.rendering.ColorGenerator
import graphics.rendering.Drawable

class RayDrawer(
    private val camera: Camera,
    private val raysProvider: () -> MutableList<RayData>,
    private var color: Color? = null
) : BaseComponent(), Drawable, Disposable {
    companion object {
        private var referenceCounter = 0
        private var buffer: PointBuffer? = null
        private var material: RayMaterial? = null
        private var shader: RayShader? = null
    }

    init {
        referenceCounter++

        if (buffer == null) {
            buffer = PointBuffer()
        }
        if (material == null) {
            material = RayMaterial()
        }
        if (shader == null) {
            shader = RayShader()
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
        material?.rayColor = color?.toVector3() ?: ColorGenerator.fromUUID(owner()!!.id).toVector3()
        material?.viewProjection = camera.viewProjection

        shader?.bind()
        raysProvider().forEach { (origin, direction, length) ->
            material?.rayOrigin = origin
            material?.rayDirection = direction
            material?.rayLength = length

            shader?.updateUniforms()
            buffer?.draw()
        }
        shader?.unbind()
    }
}