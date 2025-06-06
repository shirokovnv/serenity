package graphics.rendering.passes

import core.management.Disposable
import core.management.Resources
import graphics.rendering.shadows.ShadowFrameBuffer
import graphics.rendering.viewport.ViewportInterface

object ShadowPass : RenderPass, Disposable {
    override val name = "SHADOW_PASS"

    private var shadowFrameBuffer: ShadowFrameBuffer

    init {
        val viewport = Resources.get<ViewportInterface>()!!
        shadowFrameBuffer = ShadowFrameBuffer(viewport)
        shadowFrameBuffer.createDepthFrameBuffer()
        shadowFrameBuffer.createDepthMapTexture()

        Resources.put<ShadowFrameBuffer>(shadowFrameBuffer)

        println("SHADOW FRAME BUFFER INITIALIZED")
    }

    override fun start() {
        shadowFrameBuffer.attachDepthMapToDepthBuffer()
        shadowFrameBuffer.beforeRender()
    }

    override fun finish() {
        shadowFrameBuffer.afterRender()
    }

    override fun dispose() {
        shadowFrameBuffer.destroy()
    }
}