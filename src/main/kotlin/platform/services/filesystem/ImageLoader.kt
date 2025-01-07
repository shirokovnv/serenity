package platform.services.filesystem

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths

class ImageLoader {

    fun loadImage(fileName: String): IntArray {
        val imageBuffer: ByteBuffer = try {
            ioResourceToByteBuffer(fileName)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val w = BufferUtils.createIntBuffer(1)
        val h = BufferUtils.createIntBuffer(1)
        val c = BufferUtils.createIntBuffer(1)

        // Use info to read image metadata without decoding the entire image.
        if (!STBImage.stbi_info_from_memory(imageBuffer, w, h, c)) {
            throw RuntimeException("Failed to read image information: ${STBImage.stbi_failure_reason()}")
        }

        // Decode the image
        val image = STBImage.stbi_load_from_memory(imageBuffer, w, h, c, 0)
            ?: throw RuntimeException("Failed to load image: ${STBImage.stbi_failure_reason()}")
        val width = w[0]
        val height = h[0]
        val comp = c[0]
        val texId = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId)
        if (comp == 3) {
            if (width and 3 != 0) {
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2 - (width and 1))
            }
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGB,
                width,
                height,
                0,
                GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE,
                image
            )
        } else {
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                image
            )
        }

        STBImage.stbi_image_free(image)
        return intArrayOf(texId, w.get(), h.get())
    }

    fun loadImageToByteBuffer(fileName: String): ByteBuffer {
        val imageBuffer: ByteBuffer = try {
            ioResourceToByteBuffer(fileName)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val w = BufferUtils.createIntBuffer(1)
        val h = BufferUtils.createIntBuffer(1)
        val c = BufferUtils.createIntBuffer(1)

        // Use info to read image metadata without decoding the entire image.
        if (!STBImage.stbi_info_from_memory(imageBuffer, w, h, c)) {
            throw RuntimeException("Failed to read image information: " + STBImage.stbi_failure_reason())
        }

        // Decode the image
        return STBImage.stbi_load_from_memory(imageBuffer, w, h, c, 0)
            ?: throw RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason())
    }

    @Throws(IOException::class)
    private fun ioResourceToByteBuffer(resource: String): ByteBuffer {
        var buffer: ByteBuffer? = null
        val path = Paths.get(resource)

        if (Files.isReadable(path)) {
            buffer = Files.readAllBytes(path).let { bytes ->
                BufferUtils.createByteBuffer(bytes.size).put(bytes).flip()
            }
        }
        else{
            javaClass.classLoader.getResourceAsStream(resource)?.use { source ->
                val bytes = source.readBytes()
                buffer = BufferUtils.createByteBuffer(bytes.size).put(bytes).flip()
            }
        }

        return buffer!!
    }
}