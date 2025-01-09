package graphics.assets

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import platform.services.filesystem.ImageLoader

class Texture2d(fileName: String, loader: ImageLoader) : Asset {
    private var id: Int = 0
    private var width: Int = 0
    private var height: Int = 0

    init {
        val rawTextureData = loader.loadImage(fileName)
        this.id = rawTextureData[0]
        this.width = rawTextureData[1]
        this.height = rawTextureData[2]
    }

    override fun getId(): Int {
        return id
    }

    override fun create() {
        id = GL11.glGenTextures()
    }

    override fun destroy() {
        if (id != 0) {
            GL11.glDeleteTextures(id)
        }
        id = 0
    }

    override fun bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
    }

    override fun unbind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    fun getWidth(): Int = width

    fun getHeight(): Int = height

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