package graphics.rendering.gizmos

import core.ecs.BaseComponent
import core.management.Disposable
import core.management.Resources
import core.math.Rect3d
import core.scene.BoxAABB
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.rendering.Color
import graphics.rendering.ColorGenerator
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass

class BoxAABBRenderer(private var color: Color? = null) : BaseComponent(), Renderer, Disposable {
    companion object {
        private var referenceCounter = 0
        private var buffer: BoxAABBBuffer? = null
        private var material: BoxAABBMaterial? = null
        private var shader: BoxAABBShader? = null
    }

    private val rect3d: Rect3d
        get() = owner()!!.getComponent<BoxAABB>()!!.shape()

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    init {
        if (buffer == null) {
            buffer = BoxAABBBuffer()
        }
        if (material == null) {
            material = BoxAABBMaterial()
        }
        if (shader == null) {
            shader = BoxAABBShader()
            shader!! bind material!!
            shader!!.setup()
        }
        referenceCounter++
    }

    override fun dispose() {
        referenceCounter--
        if (referenceCounter <= 0) {
            buffer?.destroy()
            shader?.destroy()
        }
    }

    override fun render(pass: RenderPass) {
        material?.boxCenter = rect3d.center
        material?.boxSize = rect3d.size()
        material?.color = color?.toVector3() ?: ColorGenerator.fromUUID(owner()!!.id).toVector3()
        material?.viewProjection = camera.viewProjection

        shader?.bind()
        shader?.updateUniforms()
        buffer?.draw()
        shader?.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}