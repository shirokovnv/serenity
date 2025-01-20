package graphics.assets.texture

import core.math.Quaternion
import core.math.toFloatArray
import graphics.assets.Asset
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43
import java.nio.FloatBuffer

typealias ProcessTextureDataCallback = (FloatBuffer) -> Unit

class Texture2d(private val width: Int, private val height: Int) : Asset {
    private var id: Int = 0

    constructor(imageData: ImageData) : this(imageData.width, imageData.height) {
        this.id = imageData.id
    }

    init {
        create()
    }

    override fun getId(): Int {
        return id
    }

    override fun create() {
        if (id == 0) {
            id = GL11.glGenTextures()
        }
    }

    override fun destroy() {
        if (id != 0) {
            GL11.glDeleteTextures(id)
            id = 0
        }
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
        GL43.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
    }

    fun wrapModeRepeat() {
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_S, GL43.GL_REPEAT)
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_T, GL43.GL_REPEAT)
    }

    fun wrapModeClampToBorder() {
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_S, GL43.GL_CLAMP_TO_BORDER)
        GL43.glTexParameteri(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_T, GL43.GL_CLAMP_TO_BORDER)
    }

    fun setBorderColor(color: Quaternion) {
        GL43.glTexParameterfv(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_BORDER_COLOR, color.toFloatArray())
    }

    fun wrapModeClampToEdge() {
        GL43.glTexParameterIi(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_S, GL43.GL_CLAMP_TO_EDGE)
        GL43.glTexParameterIi(GL43.GL_TEXTURE_2D, GL43.GL_TEXTURE_WRAP_T, GL43.GL_CLAMP_TO_EDGE)
    }

    fun processTextureData(callback: ProcessTextureDataCallback) {
        val textureBuffer = BufferUtils.createFloatBuffer(width * height * 4)

        GL43.glBindTexture(GL43.GL_TEXTURE_2D, id)
        GL43.glGetTexImage(GL43.GL_TEXTURE_2D, 0, GL43.GL_RGBA, GL43.GL_FLOAT, textureBuffer)
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, 0)

        callback(textureBuffer)
    }
}

fun texture2dPrintDataCallback(buffer: FloatBuffer) {
    for (i in 0..<buffer.capacity() / 4) {
        val r = buffer.get(i * 4)
        val g = buffer.get(i * 4 + 1)
        val b = buffer.get(i * 4 + 2)
        val a = buffer.get(i * 4 + 3)
        println("Pixel $i: R=$r, G=$g, B=$b, A=$a")
    }
}