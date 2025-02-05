package graphics.rendering.gizmos

import core.ecs.BaseComponent
import core.management.Disposable
import core.management.Resources
import core.scene.camera.Camera
import core.scene.volumes.BoxAABB
import core.scene.volumes.BoxAABBHierarchy
import graphics.assets.surface.bind
import graphics.rendering.Color
import graphics.rendering.ColorGenerator
import graphics.rendering.Drawable

class BoxAABBDrawer(private var color: Color? = null) : BaseComponent(), Drawable, Disposable {
    companion object {
        private var referenceCounter = 0
        private var buffer: PointBuffer? = null
        private var material: BoxAABBMaterial? = null
        private var shader: BoxAABBShader? = null
    }

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    init {
        referenceCounter++

        if (buffer == null) {
            buffer = PointBuffer()
        }
        if (material == null) {
            material = BoxAABBMaterial()
        }
        if (shader == null) {
            shader = BoxAABBShader()
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
        shader?.bind()
        material?.viewProjection = camera.viewProjection
        material?.boxColor = color?.toVector3() ?: ColorGenerator.fromUUID(owner()!!.id).toVector3()
        drawBounds()
        drawBoundsHierarchy()
        shader?.unbind()
    }

    private fun drawBounds() {
        if (owner()!!.hasComponent<BoxAABB>()) {
            val rect3d = owner()!!.getComponent<BoxAABB>()!!.shape()
            material?.boxCenter = rect3d.center
            material?.boxSize = rect3d.size()
            shader?.updateUniforms()
            buffer?.draw()
        }
    }

    private fun drawBoundsHierarchy() {
        owner()!!.getComponent<BoxAABBHierarchy>()?.transformedInnerBounds()?.forEach {
            val rect3d = it.shape()
            material?.boxCenter = rect3d.center
            material?.boxSize = rect3d.size()
            shader?.updateUniforms()
            buffer?.draw()
        }
    }
}