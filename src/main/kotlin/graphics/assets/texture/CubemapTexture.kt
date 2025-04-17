package graphics.assets.texture

import core.management.Disposable
import org.lwjgl.opengl.GL43

class CubemapTexture(
    private val cubeTextureId: Int
) : Disposable {

    init {
        require(cubeTextureId != 0)
    }

    fun getId(): Int {
        return cubeTextureId
    }

    fun bind() {
        GL43.glBindTexture(GL43.GL_TEXTURE_CUBE_MAP, cubeTextureId)
    }

    fun unbind() {
        GL43.glBindTexture(GL43.GL_TEXTURE_CUBE_MAP, cubeTextureId)
    }

    override fun dispose() {
        GL43.glDeleteTextures(cubeTextureId)
    }
}