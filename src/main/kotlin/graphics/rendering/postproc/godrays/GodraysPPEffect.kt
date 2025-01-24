package graphics.rendering.postproc.godrays

import core.math.Vector2
import graphics.assets.buffer.DepthBufferType
import graphics.assets.buffer.Fbo
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.passes.PostProcPass
import graphics.rendering.postproc.PostProcEffect
import org.lwjgl.opengl.GL43

class GodraysPPEffect(
    private val lightScreenPositionProvider: () -> Vector2?
) : PostProcEffect() {

    override var fbo: Fbo? = Fbo(viewport.getWidth(), viewport.getHeight(), DepthBufferType.NONE)
    private var material = GodraysPPMaterial()
    private var shader = GodraysPPShader()

    init {
        shader bind material
        shader.setup()
    }

    override fun render(inputImage: Texture2d) {
        fbo?.bind() ?: GL43.glBindFramebuffer(GL43.GL_FRAMEBUFFER, 0)
        GL43.glViewport(0, 0, viewport.getWidth(), viewport.getHeight())
        GL43.glClear(GL43.GL_COLOR_BUFFER_BIT)

        material.lightScreenPosition = lightScreenPositionProvider()
        material.firstPass = inputImage
        material.secondPass = PostProcPass.getColorTexture()
        shader.bind()
        shader.updateUniforms()
        screenQuad.draw()
        shader.unbind()
        fbo?.unbind()
    }
}