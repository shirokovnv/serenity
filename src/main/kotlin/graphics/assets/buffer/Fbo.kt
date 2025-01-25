package graphics.assets.buffer

import graphics.assets.Asset
import graphics.assets.texture.Texture2d
import org.lwjgl.opengl.GL43.*
import java.nio.ByteBuffer

class Fbo(
    private var width: Int,
    private var height: Int,
    private val depthBufferType: DepthBufferType = DepthBufferType.DEPTH_RENDER_BUFFER,
    private val multisample: Boolean = false,
    private val numSamples: Int = 4
) : Asset {

    private var frameBuffer = 0
    private var depthBuffer = 0
    private lateinit var colorTexture: Texture2d
    private lateinit var depthTexture: Texture2d

    init {
        create()
    }

    fun setWidth(width: Int) {
        this.width = width
    }

    fun setHeight(height: Int) {
        this.height = height
    }

    override fun getId(): Int {
        return frameBuffer
    }

    override fun create() {
        if (frameBuffer != 0) {
            return
        }

        frameBuffer = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer)
        glDrawBuffer(GL_COLOR_ATTACHMENT0)

        createColorTextureAttachment()
        createDepthBufferAttachment()

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw IllegalStateException("Framebuffer is not complete.")
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    override fun bind() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer)
    }

    override fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    override fun destroy() {
        if (frameBuffer != 0 && depthBuffer != 0) {
            glDeleteFramebuffers(frameBuffer)
            glDeleteRenderbuffers(depthBuffer)
        }

        if (::colorTexture.isInitialized) {
            colorTexture.destroy()
        }
        if (::depthTexture.isInitialized) {
            depthTexture.destroy()
        }

        frameBuffer = 0
        depthBuffer = 0
    }

    fun bindForReading() {
        glBindTexture(GL_TEXTURE_2D, 0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer)
        glReadBuffer(GL_COLOR_ATTACHMENT0)
    }

    fun getColorTexture(): Texture2d = colorTexture
    fun getDepthTexture(): Texture2d = depthTexture

    private fun createColorTextureAttachment() {
        colorTexture = Texture2d(width, height)
        colorTexture.bind()

        if (multisample) {
            glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, numSamples, GL_RGBA8, width, height, false)
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null as ByteBuffer?)
        }

        colorTexture.noFilter()
        colorTexture.wrapModeClampToEdge()

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture.getId(), 0)

        colorTexture.unbind()
    }

    private fun createDepthBufferAttachment() {
        when (depthBufferType) {
            DepthBufferType.DEPTH_TEXTURE -> {
                depthTexture = Texture2d(width, height)
                depthTexture.bind()

                if (multisample) {
                    glTexImage2DMultisample(
                        GL_TEXTURE_2D_MULTISAMPLE,
                        numSamples,
                        GL_DEPTH_COMPONENT24,
                        width,
                        height,
                        false
                    )
                } else {
                    glTexImage2D(
                        GL_TEXTURE_2D,
                        0,
                        GL_DEPTH_COMPONENT24,
                        width,
                        height,
                        0,
                        GL_DEPTH_COMPONENT,
                        GL_FLOAT,
                        null as ByteBuffer?
                    )
                }

                depthTexture.noFilter()

                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.getId(), 0)

                depthTexture.unbind()
            }

            DepthBufferType.DEPTH_RENDER_BUFFER -> {
                depthBuffer = glGenRenderbuffers()
                glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer)
                glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height)
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer)
                glBindRenderbuffer(GL_RENDERBUFFER, 0)
            }

            DepthBufferType.NONE -> {}
        }
    }
}