package graphics.rendering.postproc.identity

import graphics.assets.buffer.DepthBufferType
import graphics.assets.buffer.Fbo
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.postproc.PostProcEffect
import graphics.rendering.postproc.ScreenQuad
import graphics.rendering.postproc.defaultScreenQuadProvider
import org.lwjgl.opengl.GL43.*

class IdentityPPEffect: PostProcEffect() {

    override var fbo: Fbo? = Fbo(viewport.getWidth(), viewport.getHeight(), DepthBufferType.NONE)
    private var material: IdentityPPMaterial = IdentityPPMaterial()
    private var shader: IdentityPPShader = IdentityPPShader()

    private val screenQuad: ScreenQuad
        get() = defaultScreenQuadProvider()

    init {
        shader bind material
        shader.setup()
    }

    override fun render(inputImage: Texture2d) {
        fbo?.bind() ?: glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glViewport(0, 0, viewport.getWidth(), viewport.getHeight())
        glClear(GL_COLOR_BUFFER_BIT)

        material.colorTexture = inputImage
        shader.bind()
        shader.updateUniforms()
        screenQuad.draw()
        shader.unbind()
        fbo?.unbind()
    }

    override fun getOutputImage(): Texture2d {
        return fbo?.getColorTexture() ?: Texture2d(viewport.getWidth(), viewport.getHeight())
    }
}