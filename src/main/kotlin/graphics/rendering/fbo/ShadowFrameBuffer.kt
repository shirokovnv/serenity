package graphics.rendering.fbo

import core.math.Quaternion
import graphics.assets.texture.Texture2d
import graphics.rendering.viewport.ViewportInterface
import org.lwjgl.opengl.GL43.*
import java.nio.ByteBuffer
import kotlin.properties.Delegates

class ShadowFrameBuffer(
    private var viewport: ViewportInterface,
    private var width: Int = DEFAULT_WIDTH,
    private var height: Int = DEFAULT_HEIGHT
) {
    companion object {
        const val DEFAULT_WIDTH = 4096
        const val DEFAULT_HEIGHT = 4096
    }

    private var depthMapFbo by Delegates.notNull<Int>()
    private lateinit var depthMap: Texture2d

    fun createDepthFrameBuffer() {
        depthMapFbo = glGenFramebuffers()
    }

    fun createDepthMapTexture() {
        depthMap = Texture2d(width, height)
        depthMap.bind()
        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_DEPTH_COMPONENT,
            width,
            height,
            0,
            GL_DEPTH_COMPONENT,
            GL_FLOAT,
            null as ByteBuffer?
        )
        depthMap.noFilter()
//        depthMap.wrapModeClampToEdge()
        depthMap.wrapModeClampToBorder()
        depthMap.setBorderColor(Quaternion(1f))
        depthMap.unbind()
    }

    fun attachDepthMapToDepthBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0)
        glDrawBuffer(GL_NONE)
        glReadBuffer(GL_NONE)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun beforeRender() {
        glViewport(0, 0, width, height)
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFbo)
        glClear(GL_DEPTH_BUFFER_BIT)
    }

    fun afterRender() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        glViewport(0, 0, viewport.getWidth(), viewport.getHeight())
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun destroy() {
        if (depthMapFbo != 0) {
            glDeleteBuffers(depthMapFbo)
            depthMapFbo = 0
        }

        if (::depthMap.isInitialized) {
            depthMap.destroy()
        }
    }

    fun getDepthMap(): Texture2d = depthMap
}