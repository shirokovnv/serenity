package graphics.rendering.postproc

import graphics.assets.texture.Texture2d
import org.lwjgl.opengl.GL43.*

object PostProcessor {
    private val postProcEffects = mutableListOf<PostProcEffect>()

    fun add(effect: PostProcEffect) {
        postProcEffects.add(effect)
    }

    fun remove(effect: PostProcEffect) {
        postProcEffects.removeIf { it == effect }
    }

    fun clear() {
        postProcEffects.clear()
    }

    fun process(colorImage: Texture2d) {
        glDisable(GL_DEPTH_TEST)

        var image = colorImage
        postProcEffects.withIndex().forEach { (index, effect) ->
            if (index == postProcEffects.lastIndex) {
                effect.fbo = null
            }

            effect.render(image)
            image = effect.getOutputImage()
        }

        glEnable(GL_DEPTH_TEST)
    }
}