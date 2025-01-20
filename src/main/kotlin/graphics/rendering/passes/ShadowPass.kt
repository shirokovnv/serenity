package graphics.rendering.passes

import core.scene.Object
import graphics.rendering.fbo.ShadowFrameBuffer
import graphics.rendering.viewport.ViewportInterface

object ShadowPass : RenderPass {
    override val name = "SHADOW_PASS"

    private var shadowFrameBuffer: ShadowFrameBuffer

    init {
        val viewport = Object.services.getService<ViewportInterface>()!!
        shadowFrameBuffer = ShadowFrameBuffer(viewport)
        shadowFrameBuffer.createDepthFrameBuffer()
        shadowFrameBuffer.createDepthMapTexture()

        Object.services.putService<ShadowFrameBuffer>(shadowFrameBuffer)

        println("SHADOW FRAME BUFFER INITIALIZED")
    }

    override fun start() {
        shadowFrameBuffer.attachDepthMapToDepthBuffer()
        shadowFrameBuffer.beforeRender()
    }

    override fun finish() {
        shadowFrameBuffer.afterRender()
    }
}