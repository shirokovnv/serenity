package graphics.assets.texture

import graphics.assets.Asset
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class Texture2d(private var textureData: ImageData) : Asset {

    override fun getId(): Int {
        return textureData.id
    }

    override fun create() {
        textureData = ImageData(GL11.glGenTextures(), 0, 0)
    }

    override fun destroy() {
        if (textureData.id != 0) {
            GL11.glDeleteTextures(textureData.id)
        }
        textureData = ImageData.empty()
    }

    override fun bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureData.id)
    }

    override fun unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    fun getWidth(): Int = textureData.width

    fun getHeight(): Int = textureData.height

    fun noFilter() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
    }

    fun bilinearFilter() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
    }

    fun trilinearFilter() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
    }
}